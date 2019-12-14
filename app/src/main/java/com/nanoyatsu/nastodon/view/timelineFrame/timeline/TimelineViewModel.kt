package com.nanoyatsu.nastodon.view.timelineFrame.timeline

import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiTimelines
import com.nanoyatsu.nastodon.data.api.entity.Status
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import retrofit2.HttpException
import retrofit2.Response

typealias TimelineGetter = (suspend (MastodonApiTimelines, String, String?, String?) -> Response<Array<Status>>)

class TimelineViewModel(
    val getMethod: GetMethod,
    private val auth: AuthInfo,
    private val apiManager: MastodonApiManager
) : ViewModel() {
    enum class GetMethod(val getter: TimelineGetter) {
        HOME(::callHomeTimeline),
        LOCAL(::callLocalPublicTimeline),
        FEDERATED(::callFederatedPublicTimeline)
    }

    suspend fun getByApi(getter: suspend () -> Response<Array<Status>>): Array<Status> {
        return try {
            val res = getter()
            res.body() ?: arrayOf() // todo レスポンスが期待通りじゃないときの処理 res.errorBody()
        } catch (e: HttpException) {
            e.printStackTrace()
            // todo 通信失敗のときの処理
            arrayOf()
        }
    }

    companion object {
        suspend fun callHomeTimeline(
            apiDir: MastodonApiTimelines, token: String, maxId: String?, sinceId: String?
        ) = apiDir.getHomeTimeline(token, maxId, sinceId)

        suspend fun callLocalPublicTimeline(
            apiDir: MastodonApiTimelines, token: String, maxId: String?, sinceId: String?
        ) = apiDir.getPublicTimeline(
            authorization = token, local = true, maxId = maxId, sinceId = sinceId
        )

        suspend fun callFederatedPublicTimeline(
            apiDir: MastodonApiTimelines, token: String, maxId: String?, sinceId: String?
        ) = apiDir.getPublicTimeline(
            authorization = token, local = false, maxId = maxId, sinceId = sinceId
        )
    }
}
