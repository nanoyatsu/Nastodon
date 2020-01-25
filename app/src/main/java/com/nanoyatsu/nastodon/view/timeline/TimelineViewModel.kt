package com.nanoyatsu.nastodon.view.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.nanoyatsu.nastodon.components.networkState.NetworkState
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiTimelines
import com.nanoyatsu.nastodon.data.api.entity.APIStatus
import com.nanoyatsu.nastodon.data.database.dao.TimelineDao
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.data.entity.Status
import com.nanoyatsu.nastodon.data.repository.timeline.TimelineRepository
import retrofit2.Response

typealias TimelineGetter = (suspend (MastodonApiTimelines, String, String?, String?) -> Response<List<APIStatus>>)

class TimelineViewModel(
    kind: Kind,
    auth: AuthInfo,
    timelineDao: TimelineDao,
    apiManager: MastodonApiManager
) : ViewModel() {
    enum class Kind(val getter: TimelineGetter) {
        HOME(::homeTimelineApiProvider),
        LOCAL(::localTimelineApiProvider),
        FEDERATED(::federatedTimelineApiProvider)
    }

    private val repoResult =
        TimelineRepository(timelineDao, apiManager.timelines, auth.accessToken).posts(kind)
    val statuses = repoResult.pagedList
    val networkState = repoResult.networkState
    val isInitialising = repoResult.refreshState
    private val refresh = repoResult.refresh
    private val retry = repoResult.retry

    val _statuses: LiveData<PagedList<Status>>
    val _networkState: LiveData<NetworkState>
    val _isInitialising: LiveData<Boolean>
    private val _refresh: () -> Unit
    private val _retry: () -> Unit

    init {
        val sourceFactory =
            TimelineDataSourceFactory(kind, apiManager.timelines, auth.accessToken)
        _statuses = LivePagedListBuilder<String, Status>(sourceFactory, TIMELINE_PAGE_SIZE).build()
        _networkState = switchMap(sourceFactory.sourceLiveData) { it.networkState }
        _isInitialising = switchMap(sourceFactory.sourceLiveData) { it.isInitialising }
        _refresh = { sourceFactory.sourceLiveData.value?.invalidate() }
        _retry = { sourceFactory.sourceLiveData.value?.retryAllFailed() }
    }

    fun refreshTimeline() = refresh.invoke()
    fun retry() = retry.invoke()

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
