package com.nanoyatsu.nastodon.view.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class TimelineFrameViewModel @Inject constructor() : ViewModel() {

    // vm
    private val _tootEvent = MutableLiveData<Boolean>().apply { value = false }
    val tootEvent: LiveData<Boolean>
        get() = _tootEvent

    fun onTootClicked() = run { _tootEvent.value = true }
    fun onTootClickFinished() = run { _tootEvent.value = false }

}