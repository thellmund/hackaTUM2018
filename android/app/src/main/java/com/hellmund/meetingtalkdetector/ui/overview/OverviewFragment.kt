package com.hellmund.meetingtalkdetector.ui.overview

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.hellmund.meetingtalkdetector.R
import com.hellmund.meetingtalkdetector.data.Recording
import com.hellmund.meetingtalkdetector.data.RecordingsStore
import com.hellmund.meetingtalkdetector.ui.recording.RecordingFragment
import com.hellmund.meetingtalkdetector.ui.recordingdetails.RecordingDetailsFragment
import com.hellmund.meetingtalkdetector.ui.shared.BaseFragment
import com.hellmund.meetingtalkdetector.util.ServerUtils
import kotlinx.android.synthetic.main.dialog_input_view.view.*
import kotlinx.android.synthetic.main.fragment_overview.*




class OverviewFragment : BaseFragment() {

    private var adapter: RecordingsAdapter? = null

    private val viewModel: OverviewViewModel by lazy {
        val store = RecordingsStore.getInstance(requireContext())
        val factory = OverviewViewModel.Factory(store)
        ViewModelProviders.of(this, factory).get(OverviewViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA),
            REQUEST_CODE_CAMERA
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().title = getString(R.string.app_name)
        initRecyclerView()
        startRecordingButton.setOnClickListener {
            showTitleDialog()
        }

        if (ServerUtils.getServerAddress(requireContext()).isBlank()) {
            showServerAddressDialog()
        }

        viewModel.recordings.observe(viewLifecycleOwner, Observer {
            updateView(it)
        })
    }

    private fun showTitleDialog() {
        val view = View.inflate(requireContext(), R.layout.dialog_input_view, null)
        val editText = view.titleEditText

        AlertDialog
            .Builder(requireContext())
            .setView(view)
            .setCancelable(true)
            .setPositiveButton("Create") { _, _ ->
                val title = editText.text.toString()
                openRecordingFragment(title)
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()

        editText.onFocusChangeListener = View.OnFocusChangeListener { _, _ ->
            editText.post {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
            }
        }
        editText.requestFocus()
    }

    private fun initRecyclerView() {
        recordingsRecyclerView.setHasFixedSize(true)
        recordingsRecyclerView.itemAnimator = DefaultItemAnimator()
        recordingsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = RecordingsAdapter(this::openRecordingDetails, this::showDeleteDialog)
        recordingsRecyclerView.adapter = adapter
    }

    private fun openRecordingDetails(recording: Recording) {
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.contentFrame,
                RecordingDetailsFragment.newInstance(recording)
            )
            .addToBackStack(null)
            .commit()
    }

    private fun showDeleteDialog(recording: Recording, position: Int) {
        AlertDialog.Builder(requireContext())
            .setMessage("Delete ${recording.title}?")
            .setPositiveButton("Delete") { _, _ ->
                RecordingsStore.getInstance(requireContext()).delete(recording)
                Toast.makeText(requireContext(), "${recording.title} deleted", Toast.LENGTH_LONG).show()
                adapter?.remove(position)
            }
            .setNegativeButton("Cancel", null)
            .setCancelable(true)
            .show()
    }

    private fun updateView(recordings: List<Recording>) {
        adapter?.update(recordings.sortedByDescending { it.timestamp })
    }

    private fun openRecordingFragment(title: String) {
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.contentFrame,
                RecordingFragment.newInstance(title)
            )
            .addToBackStack(null)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_server_address -> {
                showServerAddressDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showServerAddressDialog() {
        val view = View.inflate(requireContext(), R.layout.dialog_input_view, null)
        val editText = view.titleEditText
        editText.hint = "Server address"
        editText.setText(ServerUtils.getServerAddress(requireContext()))

        AlertDialog
            .Builder(requireContext())
            .setView(view)
            .setCancelable(true)
            .setPositiveButton("Set server address") { _, _ ->
                ServerUtils.setServerAddress(requireContext(), editText.text.toString())
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()

        editText.onFocusChangeListener = View.OnFocusChangeListener { _, _ ->
            editText.post {
                // Immediately show the keyboard when the dialog opens
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
            }
        }
        editText.requestFocus()
    }

    companion object {

        private const val REQUEST_CODE_CAMERA = 1

        @JvmStatic
        fun newInstance() = OverviewFragment()

    }

}
