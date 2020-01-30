package com.nanoyatsu.nastodon.view.timeline

import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiTimelines
import com.nanoyatsu.nastodon.data.api.entity.APIStatus
import com.nanoyatsu.nastodon.data.repository.timeline.TimelineRepository
import retrofit2.Response

typealias TimelineGetter = (suspend (MastodonApiTimelines, String, String?, String?) -> Response<List<APIStatus>>)

class TimelineViewModel(repo: TimelineRepository) : ViewModel() {
    enum class Kind(val getter: TimelineGetter) {
        HOME(::homeTimelineApiProvider),
        LOCAL(::localTimelineApiProvider),
        FEDERATED(::federatedTimelineApiProvider)
    }

    private val repoResult = repo.posts()
    val statuses = repoResult.pagedList
    val networkState = repoResult.networkState
    val isInitialising = repoResult.isRefreshing
    private val refresh = repoResult.refresh
    private val retry = repoResult.retry

    fun refresh() = refresh.invoke()
    fun retry() = retry.invoke()

    companion object {
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
