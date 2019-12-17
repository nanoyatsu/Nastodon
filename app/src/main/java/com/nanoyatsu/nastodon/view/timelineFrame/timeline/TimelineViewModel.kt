package com.nanoyatsu.nastodon.view.timelineFrame.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiTimelines
import com.nanoyatsu.nastodon.data.api.entity.Status
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import retrofit2.Response

typealias TimelineGetter = (suspend (MastodonApiTimelines, String, String?, String?) -> Response<List<Status>>)

class TimelineViewModel(
    private val kind: Kind,
    private val auth: AuthInfo,
    private val apiManager: MastodonApiManager
) : ViewModel() {
    enum class Kind(val getter: TimelineGetter) {
        HOME(::homeTimelineApiProvider),
        LOCAL(::localTimelineApiProvider),
        FEDERATED(::federatedTimelineApiProvider)
    }

    val timeline: LiveData<PagedList<Status>> = run {
        val factory = TimelineDataSourceFactory(kind, apiManager.timelines, auth.accessToken)
        LivePagedListBuilder<String, Status>(factory, TIMELINE_PAGE_SIZE).build()
    }

    fun clearTimeline() {
        val factory = TimelineDataSourceFactory(kind, apiManager.timelines, auth.accessToken)
        // todo 再生成
        val dummy = LivePagedListBuilder<String, Status>(factory, 20).build()
    }

    companion object {
        const val TIMELINE_PAGE_SIZE = 20

        suspend fun homeTimelineApiProvider(
            apiDir: MastodonApiTimelines, token: String, maxId: String?, sinceId: String?
        ) = apiDir.getHomeTimeline(token, maxId, sinceId)

        suspend fun localTimelineApiProvider(
            apiDir: MastodonApiTimelines, token: String, maxId: String?, sinceId: String?
        ) = apiDir.getPublicTimeline(
            authorization = token, local = true, maxId = maxId, sinceId = sinceId
        )

        suspend fun federatedTimelineApiProvider(
            apiDir: MastodonApiTimelines, token: String, maxId: String?, sinceId: String?
        ) = apiDir.getPublicTimeline(
            authorization = token, local = false, maxId = maxId, sinceId = sinceId
        )
    }
}
