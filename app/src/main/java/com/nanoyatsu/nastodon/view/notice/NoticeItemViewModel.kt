package com.nanoyatsu.nastodon.view.notice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nanoyatsu.nastodon.data.domain.Notification

/**
 * 画面と紐付かない形でのみ使っているので、androidx.lifecycle.ViewModelを継承していない
 */
class NoticeItemViewModel(val notice: Notification) {
    private val _contentClickEvent = MutableLiveData<Boolean>().apply { value = false }
    val contentClickEvent: LiveData<Boolean> get() = _contentClickEvent
    // todo avatarClickEvent -> navigate AccountDetail

    fun onContentClicked() = run { _contentClickEvent.value = true }
    fun onContentClickFinished() = run { _contentClickEvent.value = false }
}
