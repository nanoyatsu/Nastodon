package com.nanoyatsu.nastodon.view.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.R

class TimelineFrameViewModel() : ViewModel() {
    var selectedTabId: Int = R.id.navigation_timeline

    private val _progressVisible = MutableLiveData<Boolean>()
    val progressVisible: LiveData<Boolean>
        get() = _progressVisible

    fun progressStart() {
        _progressVisible.value = true
    }

    fun progressEnd() {
        _progressVisible.value = false
    }

}
