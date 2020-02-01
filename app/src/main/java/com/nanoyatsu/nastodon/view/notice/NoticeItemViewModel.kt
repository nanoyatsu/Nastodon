package com.nanoyatsu.nastodon.view.notice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nanoyatsu.nastodon.data.domain.Notification

/**
 * 画面と紐付かない形でのみ使っているので、androidx.lifecycle.ViewModelを継承していない
 */
class NoticeItemViewModel(val notice: Notification) {

    private val _avatarClickEvent = MutableLiveData<Boolean>().apply { value = false }
    val avatarClickEvent: LiveData<Boolean> get() = _avatarClickEvent
    private val _contentClickEvent = MutableLiveData<Boolean>().apply { value = false }
    val contentClickEvent: LiveData<Boolean> get() = _contentClickEvent
    // todo avatarClickEvent -> navigate AccountDetail

    fun onAvatarClicked() = run { _avatarClickEvent.value = true }
    fun onAvatarClickFinished() = run { _avatarClickEvent.value = false }
    fun onContentClicked() = run { _contentClickEvent.value = true }
    fun onContentClickFinished() = run { _contentClickEvent.value = false }
}
