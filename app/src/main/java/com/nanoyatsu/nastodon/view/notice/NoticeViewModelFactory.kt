package com.nanoyatsu.nastodon.view.notice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nanoyatsu.nastodon.data.repository.notice.NoticeRepository

class NoticeViewModelFactory(
    private val repo: NoticeRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoticeViewModel::class.java)) {
            return NoticeViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}