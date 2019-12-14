package com.nanoyatsu.nastodon.view.timelineFrame.timeline

import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.entity.Status
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import retrofit2.HttpException
import retrofit2.Response

class TimelineViewModel(
    val getMethod: GetMethod,
    private val auth: AuthInfo,
    private val apiManager: MastodonApiManager
) : ViewModel() {
    enum class GetMethod { HOME, LOCAL, GLOBAL }

    // todo 同じ型シグネチャで取得関数を用意する enumに紐付けたいが、staticとの絡みで案が必要
    private suspend fun callHomeTimeline(maxId: String?, sinceId: String?) =
        apiManager.timelines.getHomeTimeline(auth.accessToken, maxId, sinceId)

    private suspend fun callLocalPublicTimeline(maxId: String?, sinceId: String?) =
        apiManager.timelines.getPublicTimeline(
            authorization = auth.accessToken, local = true, maxId = maxId, sinceId = sinceId
        )

    private suspend fun callGlobalPublicTimeline(maxId: String?, sinceId: String?) =
        apiManager.timelines.getPublicTimeline(
            authorization = auth.accessToken, local = false, maxId = maxId, sinceId = sinceId
        )

    fun returnTimelineGetter(getMethod: GetMethod): suspend ((String?, String?) -> Response<Array<Status>>) {
        val callApi = when (getMethod) {
            GetMethod.HOME -> ::callHomeTimeline
            GetMethod.LOCAL -> ::callLocalPublicTimeline
            GetMethod.GLOBAL -> ::callGlobalPublicTimeline
        }
        return { maxId: String?, sinceId: String? -> callApi(maxId, sinceId) }
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

}
