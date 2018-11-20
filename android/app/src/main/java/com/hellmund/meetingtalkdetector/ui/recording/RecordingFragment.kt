package com.hellmund.meetingtalkdetector.ui.recording

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.hellmund.meetingtalkdetector.R
import com.hellmund.meetingtalkdetector.data.*
import com.hellmund.meetingtalkdetector.networking.SocketClient
import com.hellmund.meetingtalkdetector.ui.shared.BaseFragment
import com.hellmund.meetingtalkdetector.ui.shared.PersonStatesAdapter
import com.hellmund.meetingtalkdetector.util.ServerUtils
import com.hellmund.meetingtalkdetector.util.copyToBitmap
import com.hellmund.meetingtalkdetector.util.rotatedBy
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.preview.Frame
import io.fotoapparat.selector.*
import kotlinx.android.synthetic.main.fragment_recording.*
import org.jetbrains.anko.support.v4.runOnUiThread
import java.net.URI

class RecordingFragment : BaseFragment(), SocketClient.Listener {

    private var fotoapparat: Fotoapparat? = null

    private lateinit var bitmap: Bitmap
    private var hasRequestedThumbnails = false

    private var adapter: PersonStatesAdapter? = null

    private val recordingTitle: String by lazy {
        arguments?.getString(KEY_TITLE) ?: throw IllegalStateException()
    }

    private var recordingState = RecordingState.create()
    private var recordingTimestamp = System.currentTimeMillis()

    private var lastViewUpdate = 0L

    private var socketClient: SocketClient? = null

    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recording, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.recording_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            android.R.id.home -> {
                showDiscardDialog()
                true
            }
            R.id.menu_save -> {
                finishRecording()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showDiscardDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Discard changes?")
            .setPositiveButton("Discard") { _, _ ->
                requireActivity().supportFragmentManager.popBackStack()
            }
            .setNegativeButton("Cancel", null)
            .setCancelable(false)
            .show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().title = recordingTitle
        initRecyclerView()
    }

    private fun initRecyclerView() {
        personStatesRecyclerView.setHasFixedSize(true)
        personStatesRecyclerView.itemAnimator = DefaultItemAnimator()
        personStatesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = PersonStatesAdapter()
        personStatesRecyclerView.adapter = adapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initCameraView()
        initSocketClient()
    }

    private fun initSocketClient() {
        val serverAddress = ServerUtils.getServerAddress(requireContext())
        val uri = URI.create("ws://$serverAddress:8000/ws")
        socketClient = SocketClient(uri, this)
        socketClient?.connect()
    }

    override fun onStart() {
        super.onStart()
        fotoapparat?.start()
    }

    private fun initCameraView() {
        val config = CameraConfiguration(
            focusMode = firstAvailable(continuousFocusPicture(), autoFocus(), fixed()),
            previewFpsRange = exactFixedFps(30f),
            frameProcessor = { frame -> processFrame(frame) }
        )

        fotoapparat = Fotoapparat(
            requireContext(),
            cameraView,
            cameraConfiguration = config,
            lensPosition = back(),
            scaleType = ScaleType.CenterCrop
        )
    }

    override fun onSocketOpened() {
        runOnUiThread {
            Toast.makeText(requireContext(), "Connected to server", Toast.LENGTH_LONG).show()
        }
    }

    override fun onSocketClosed() {
        Log.d(RecordingFragment::class.java.simpleName, "Socket closed")
    }

    override fun onSocketMessage(message: String) {
        val response = gson.fromJson<Response>(message, Response::class.java)
        onResponseReceived(response)
    }

    override fun onSocketError(e: Exception?) {
        runOnUiThread {
            Toast.makeText(requireContext(), "Socket error", Toast.LENGTH_LONG).show()
        }
    }

    private var imageIndex = 0

    private fun processFrame(frame: Frame) {
        imageIndex += 1
        if (imageIndex % 6 != 0) {
            // Only every third image
            return
        }

        if (::bitmap.isInitialized.not()) {
            bitmap = Bitmap.createBitmap(frame.size.width, frame.size.height, Bitmap.Config.ARGB_8888)
        }

        socketClient?.let {
            if (it.isConnected) {
                frame.copyToBitmap(bitmap, 500)
                sendBitmap(bitmap)
            }
        }
    }

    private fun sendBitmap(bitmap: Bitmap) {
        val request = Request.create(bitmap.rotatedBy(90f), requiresThumbnails = hasRequestedThumbnails.not())
        val value = gson.toJson(request)
        socketClient?.sendAsync(value)
    }

    private fun onResponseReceived(response: Response) {
        val speakingParticipantIds = response.talking.toSet()
        val spokenDuration = TIME_BETWEEN_FRAMES_IN_MILLIS

        val currentState = recordingState.currentTalkingState
        val currentStateIds = recordingState.currentTalkingState.map { it.id }

        val speakingParticipants = speakingParticipantIds.map { PersonState(it, null, spokenDuration) }
        val newParticipants = speakingParticipants.filter { currentStateIds.contains(it.id).not() }
        val existingParticipants = speakingParticipants.filter { currentStateIds.contains(it.id) }

        val updatedExistingParticipants = existingParticipants.map { newExisting ->
            val oldExisting = currentState.first {  it.id == newExisting.id }
            oldExisting.copy(talkingTime = oldExisting.talkingTime + newExisting.talkingTime)
        }

        val nonSpeakingParticipants = recordingState.currentTalkingState.filter {
            speakingParticipantIds.contains(it.id).not()
        }

        val updatedParticipants = nonSpeakingParticipants + updatedExistingParticipants + newParticipants
        recordingState = recordingState.copy(currentTalkingState = updatedParticipants)
        updateView(recordingState)
    }

    private fun updateView(recordingState: RecordingState) {
        Log.d(RecordingFragment::class.java.simpleName, recordingState.toString())
        val now = System.currentTimeMillis()

        if (now - lastViewUpdate <= 1000) {
            return
        }

        lastViewUpdate = now

        runOnUiThread {
            adapter?.update(recordingState.currentTalkingState.sortedByDescending { it.talkingTime })
        }
    }

    private fun finishRecording() {
        val recording = Recording(recordingTitle, recordingTimestamp, recordingState.currentTalkingState)
        RecordingsStore.getInstance(requireContext()).store(recording)
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun onStop() {
        super.onStop()
        fotoapparat?.stop()
        socketClient?.close()
    }

    companion object {

        private const val TIME_BETWEEN_FRAMES_IN_MILLIS = 200L
        private const val KEY_TITLE = "title"

        @JvmStatic
        fun newInstance(title: String) = RecordingFragment().apply {
            arguments = Bundle().apply {
                putString(KEY_TITLE, title)
            }
        }

    }
}
