package com.hellmund.meetingtalkdetector.ui.recordingdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.hellmund.meetingtalkdetector.R
import com.hellmund.meetingtalkdetector.data.Recording
import com.hellmund.meetingtalkdetector.ui.shared.BaseFragment
import com.hellmund.meetingtalkdetector.ui.shared.PersonStatesAdapter
import kotlinx.android.synthetic.main.fragment_recording_details.*

class RecordingDetailsFragment : BaseFragment() {

    private var adapter: PersonStatesAdapter? = null

    private val recording: Recording by lazy {
        arguments?.getParcelable(KEY_RECORDING) as? Recording ?: throw IllegalStateException()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recording_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().title = recording.title
        initRecyclerView()
    }

    private fun initRecyclerView() {
        personStatesRecyclerView.setHasFixedSize(true)
        personStatesRecyclerView.itemAnimator = DefaultItemAnimator()
        personStatesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = PersonStatesAdapter()
        personStatesRecyclerView.adapter = adapter
        adapter?.update(recording.personStates)
    }

    companion object {

        private const val KEY_RECORDING = "recording"

        @JvmStatic
        fun newInstance(recording: Recording) = RecordingDetailsFragment().apply {
            arguments = Bundle().apply {
                putParcelable(KEY_RECORDING, recording)
            }
        }

    }

}
