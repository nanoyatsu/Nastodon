package com.nanoyatsu.nastodon.view.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimelineFrameViewModel : ViewModel() {

    // vm
    private val _tootEvent = MutableLiveData<Boolean>().apply { value = false }
    val tootEvent: LiveData<Boolean>
        get() = _tootEvent

    fun onTootClicked() = run { _tootEvent.value = true }
    fun onTootClickFinished() = run { _tootEvent.value = false }

}