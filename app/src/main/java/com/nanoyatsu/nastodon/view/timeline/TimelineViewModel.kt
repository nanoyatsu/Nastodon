package com.nanoyatsu.nastodon.view.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.nanoyatsu.nastodon.components.networkState.NetworkState
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiTimelines
import com.nanoyatsu.nastodon.data.entity.Status
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

    val statuses: LiveData<PagedList<Status>>
    val networkState: LiveData<NetworkState>
    val isInitialising: LiveData<Boolean>
    private val refresh: () -> Unit
    private val retry: () -> Unit

    init {
        val sourceFactory =
            TimelineDataSourceFactory(kind, apiManager.timelines, auth.accessToken)
        statuses = LivePagedListBuilder<String, Status>(sourceFactory, TIMELINE_PAGE_SIZE).build()
        networkState = switchMap(sourceFactory.sourceLiveData) { it.networkState }
        isInitialising = switchMap(sourceFactory.sourceLiveData) { it.isInitialising }
        refresh = { sourceFactory.sourceLiveData.value?.invalidate() }
        retry = { sourceFactory.sourceLiveData.value?.retryAllFailed() }
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
