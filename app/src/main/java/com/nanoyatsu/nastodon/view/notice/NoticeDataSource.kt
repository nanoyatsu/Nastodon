package com.nanoyatsu.nastodon.view.notice

import androidx.paging.ItemKeyedDataSource
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiNotifications
import com.nanoyatsu.nastodon.data.api.entity.Notification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class NoticeDataSource(
    private val noticeKind: NoticeViewModel.Kind,
    private val apiDir: MastodonApiNotifications,
    private val token: String
) : ItemKeyedDataSource<String, Notification>() {
    override fun loadInitial(
        params: LoadInitialParams<String>, callback: LoadInitialCallback<Notification>
    ) {
        val response = runBlocking(Dispatchers.IO)
        { noticeKind.getter(apiDir, token, null, null) }

        val notifications: List<Notification> = response.body() ?: emptyList()
        callback.onResult(notifications)
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<Notification>) {
        val response = runBlocking(Dispatchers.IO)
        { noticeKind.getter(apiDir, token, params.key, null) }

        val notifications: List<Notification> = response.body() ?: emptyList()
        callback.onResult(notifications)
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<Notification>) {
        // なにもしない 未来方向のloadは実装しない
    }

    override fun getKey(item: Notification): String = item.id
}