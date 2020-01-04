package com.nanoyatsu.nastodon.view.notice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo

class NoticeViewModelFactory(
    private val kind: NoticeViewModel.Kind,
    private val auth: AuthInfo,
    private val apiManager: MastodonApiManager
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoticeViewModel::class.java)) {
            return NoticeViewModel(kind, auth, apiManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}