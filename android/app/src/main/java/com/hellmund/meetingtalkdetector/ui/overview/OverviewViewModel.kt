package com.hellmund.meetingtalkdetector.ui.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hellmund.meetingtalkdetector.data.Recording
import com.hellmund.meetingtalkdetector.data.RecordingsStore
import org.jetbrains.anko.doAsync

class OverviewViewModel(
    private val recordingsStore: RecordingsStore
) : ViewModel() {

    val recordings: LiveData<List<Recording>>
        get() {
            val recording = MutableLiveData<List<Recording>>()
            doAsync {
                val data = recordingsStore.getAll()
                recording.postValue(data)
            }
            return recording
        }

    class Factory(
        private val recordingsStore: RecordingsStore
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return OverviewViewModel(recordingsStore) as T
        }

    }

}
