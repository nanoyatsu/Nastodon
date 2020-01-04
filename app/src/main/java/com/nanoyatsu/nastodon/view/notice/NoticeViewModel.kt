package com.nanoyatsu.nastodon.view.notice

import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiNotifications
import com.nanoyatsu.nastodon.data.api.entity.Notification
import com.nanoyatsu.nastodon.data.api.entity.NotificationType
import retrofit2.Response

typealias NotificationsGetter = (suspend (MastodonApiNotifications, String, String?, String?) -> Response<List<Notification>>)

class NoticeViewModel : ViewModel() {
    enum class Kind(val getter: NotificationsGetter) { ALL(::allNoticeApiProvider), REPLY(::replyNoticeApiProvider) }

    companion object {
        const val NOTICE_PAGE_SIZE = 20
        private val excludeReply =
            NotificationType.values().filterNot { it == NotificationType.mention }.map { it.name }

        suspend fun allNoticeApiProvider(
            apiDir: MastodonApiNotifications, token: String, maxId: String?, sinceId: String?
        ) = apiDir.getAllNotifications(token, maxId, sinceId, null, null, null, null)

        suspend fun replyNoticeApiProvider(
            apiDir: MastodonApiNotifications, token: String, maxId: String?, sinceId: String?
        ) = apiDir.getAllNotifications(token, maxId, sinceId, null, null, excludeReply, null)
    }
}
