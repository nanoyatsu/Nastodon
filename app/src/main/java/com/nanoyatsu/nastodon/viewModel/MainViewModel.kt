package com.nanoyatsu.nastodon.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.R

class MainViewModel() : ViewModel() {
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
