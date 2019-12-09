package com.nanoyatsu.nastodon.view.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.R

class TimelineFrameViewModel() : ViewModel() {
    val timelineTabs =
        arrayOf(R.id.navigation_timeline, R.id.navigation_notice, R.id.navigation_global_timeline)
            .zip(TimelineFragment.GetMethod.values())


    var selectedTabId: Int = R.id.navigation_timeline

    private val _progressVisible = MutableLiveData<Boolean>().apply { value = false }
    val progressVisible: LiveData<Boolean>
        get() = _progressVisible

    private val _tootEvent = MutableLiveData<Boolean>().apply { value = false }
    val tootEvent: LiveData<Boolean>
        get() = _tootEvent

    fun progressStart() = run { _progressVisible.value = true }
    fun progressEnd() = run { _progressVisible.value = false }
    fun onTootClicked() = run { _tootEvent.value = true }
    fun onTootClickFinished() = run { _tootEvent.value = false }
}
