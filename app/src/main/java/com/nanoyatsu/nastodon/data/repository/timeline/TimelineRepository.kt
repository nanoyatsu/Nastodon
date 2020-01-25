package com.nanoyatsu.nastodon.data.repository.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import com.nanoyatsu.nastodon.components.networkState.Listing
import com.nanoyatsu.nastodon.components.networkState.NetworkState
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiTimelines
import com.nanoyatsu.nastodon.data.database.dao.TimelineDao
import com.nanoyatsu.nastodon.data.entity.Status
import com.nanoyatsu.nastodon.view.timeline.TimelineViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.IOException

class TimelineRepository(
    private val dao: TimelineDao,
    private val apiDir: MastodonApiTimelines,
    private val token: String
) {
    companion object {
        const val TIMELINE_PAGE_SIZE = 20
    }

    fun posts(kind: TimelineViewModel.Kind): Listing<Status> {
        val boundaryCallback = TimelineBoundaryCallback(dao, kind, apiDir, token)

        val dataSourceFactory = dao.getTimeline(kind.ordinal).map { it.asDomainModel() }
        val livePagedList = LivePagedListBuilder(dataSourceFactory, TIMELINE_PAGE_SIZE)
            .setBoundaryCallback(boundaryCallback)
            .build()

        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) { refresh(kind) }

        return Listing(
            pagedList = livePagedList,
            networkState = boundaryCallback.networkState,
            refreshState = refreshState,
            refresh = { refreshTrigger.value = null },
            retry = { boundaryCallback.retryAllFailed() }
        )
    }

    private fun refresh(kind: TimelineViewModel.Kind): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        runBlocking(context = Dispatchers.IO) {
            try {
                val response = kind.getter(apiDir, token, null, null)
                val status = response.body()?.map { it.asDatabaseModel(kind.ordinal) }
                    ?: throw IOException("response.body() is null") // todo レスポンスエラー処理

                dao.deleteAll()
                dao.insertAll(status)
                networkState.postValue(NetworkState.LOADED)
            } catch (ioException: IOException) {
                networkState.postValue(NetworkState.error(ioException.message ?: "unknown error"))
            }
        }
        return networkState
    }
}