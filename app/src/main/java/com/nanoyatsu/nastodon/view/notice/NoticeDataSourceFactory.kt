package com.nanoyatsu.nastodon.view.notice

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiNotifications
import com.nanoyatsu.nastodon.data.domain.Notification

class NoticeDataSourceFactory(
    private val noticeKind: NoticeViewModel.Kind,
    private val apiDir: MastodonApiNotifications,
    private val token: String
) : DataSource.Factory<String, Notification>() {
    val sourceLiveData = MutableLiveData<NoticeDataSource>()
    override fun create(): DataSource<String, Notification> {
        val source = NoticeDataSource(noticeKind, apiDir, token)
        sourceLiveData.postValue(source)
        return source
    }
}