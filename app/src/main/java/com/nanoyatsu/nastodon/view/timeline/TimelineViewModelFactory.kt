package com.nanoyatsu.nastodon.view.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nanoyatsu.nastodon.data.repository.timeline.TimelineRepository

class TimelineViewModelFactory(private val repo: TimelineRepository) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimelineViewModel::class.java)) {
            return TimelineViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
