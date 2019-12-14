package com.nanoyatsu.nastodon.view.timelineFrame.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiTimelines
import com.nanoyatsu.nastodon.data.api.entity.Status
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import retrofit2.HttpException
import retrofit2.Response

typealias TimelineGetter = (suspend (MastodonApiTimelines, String, String?, String?) -> Response<List<Status>>)

class TimelineViewModel(
    private val getMethod: GetMethod,
    private val auth: AuthInfo,
    private val apiManager: MastodonApiManager
) : ViewModel() {
    enum class GetMethod(val getter: TimelineGetter) {
        HOME(::callHomeTimeline),
        LOCAL(::callLocalPublicTimeline),
        FEDERATED(::callFederatedPublicTimeline)
    }

    private val _timeline = MutableLiveData<List<Status>>().apply { value = mutableListOf() }
    val timeline: LiveData<List<Status>>
        get() = _timeline

    suspend fun reloadTimeline(maxId: String? = null, sinceId: String? = null) {
        val apiDir = apiManager.timelines
        val token = auth.accessToken
        val getter = suspend { getMethod.getter(apiDir, token, maxId, sinceId) }
        val toots = getByApi(getter)
        _timeline.value = _timeline.value?.plus(toots)
    }

    fun clearTimeline() {
        _timeline.value = mutableListOf()
    }

    private suspend fun getByApi(getter: suspend () -> Response<List<Status>>): List<Status> {
        return try {
            val res = getter()
            res.body() ?: listOf() // todo レスポンスが期待通りじゃないときの処理 res.errorBody()
        } catch (e: HttpException) {
            e.printStackTrace()
            // todo 通信失敗のときの処理
            listOf()
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
