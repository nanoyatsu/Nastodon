package com.nanoyatsu.nastodon.view.timelineFrame

import android.view.MenuItem
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.R

class TimelineFrameViewModel() : ViewModel() {
    private val _selectedTabId = MutableLiveData<Int>().apply { value = R.id.frame_tab_timeline }
    val selectedTabId: LiveData<Int>
        get() = _selectedTabId

    private val _progressVisible = MutableLiveData<Boolean>().apply { value = false }
    val progressVisible: LiveData<Boolean>
        get() = _progressVisible

    private val _tootEvent = MutableLiveData<Boolean>().apply { value = false }
    val tootEvent: LiveData<Boolean>
        get() = _tootEvent

    fun onSelectedMenuItem(item: MenuItem): Boolean {
        _selectedTabId.value = item.itemId
        return true
    }

    fun onTootClicked() = run { _tootEvent.value = true }
    fun onTootClickFinished() = run { _tootEvent.value = false }

    fun progressStart() = run { _progressVisible.value = true }
    fun progressEnd() = run { _progressVisible.value = false }
}
