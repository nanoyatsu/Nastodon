package com.nanoyatsu.nastodon.view.timelineFrame.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TimelineViewModelFactory(private val getMethod: TimelineViewModel.GetMethod) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimelineViewModel::class.java)) {
            return TimelineViewModel(getMethod) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
