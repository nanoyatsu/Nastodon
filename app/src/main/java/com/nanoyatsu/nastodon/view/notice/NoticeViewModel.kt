package com.nanoyatsu.nastodon.view.notice

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiNotifications
import com.nanoyatsu.nastodon.data.api.entity.Notification
import com.nanoyatsu.nastodon.data.api.entity.NotificationType
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import retrofit2.Response

typealias NotificationsGetter = (suspend (MastodonApiNotifications, String, String?, String?) -> Response<List<Notification>>)

class NoticeViewModel(
    private val kind: Kind,
    private val auth: AuthInfo,
    private val apiManager: MastodonApiManager
) : ViewModel() {
    enum class Kind(val getter: NotificationsGetter) { ALL(::allNoticeApiProvider), REPLY(::replyNoticeApiProvider) }

    val notifications: LiveData<PagedList<Notification>> = run {
        val factory = NoticeDataSourceFactory(kind, apiManager.notifications, auth.accessToken)
        LivePagedListBuilder<String, Notification>(factory, NOTICE_PAGE_SIZE).build()
    }

    companion object {
        const val NOTICE_PAGE_SIZE = 20
        private val excludeReply =
            NotificationType.values().filterNot { it == NotificationType.MENTION }.map { it.value }

        suspend fun allNoticeApiProvider(
            apiDir: MastodonApiNotifications, token: String, maxId: String?, sinceId: String?
        ) = apiDir.getAllNotifications(token, maxId, sinceId, null, null, null, null)

        // fixme excludeTypesとaccountIdが機能していない
        //  excludeReplyは配列の表現方法が不明(retrofit任せだと同じ名前のパラメータがたくさん出来る、"[\"mention\"]"とか試したけどダメ)
        //  accountIdはなんで機能してないのかよくわからない
        //  Notifications系AP自体の使い方に問題があるかもしれない ../clearとかがあるところから考えてもイメージと違うところがありそう
        suspend fun replyNoticeApiProvider(
            apiDir: MastodonApiNotifications, token: String, maxId: String?, sinceId: String?
        ) = apiDir.getAllNotifications(token, maxId, sinceId, null, null, excludeReply, null)
    }
}
