package com.nanoyatsu.nastodon.view.timelineFrame.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo

class TimelineViewModelFactory(
    private val kind: TimelineViewModel.Kind,
    private val auth: AuthInfo,
    private val apiManager: MastodonApiManager
) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimelineViewModel::class.java)) {
            return TimelineViewModel(kind, auth, apiManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
