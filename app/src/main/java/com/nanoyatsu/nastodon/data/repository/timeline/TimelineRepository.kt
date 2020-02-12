package com.nanoyatsu.nastodon.data.repository.timeline

import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import com.nanoyatsu.nastodon.components.networkState.Listing
import com.nanoyatsu.nastodon.components.networkState.NetworkState
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.database.dao.TimelineDao
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.data.domain.Status
import com.nanoyatsu.nastodon.view.timeline.TimelineViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class TimelineRepository(
    private val kind: TimelineViewModel.Kind,
    private val dao: TimelineDao,
    apiManager: MastodonApiManager,
    auth: AuthInfo
) {
    val apiDir = apiManager.timelines
    val token = auth.accessToken

    fun posts(): Listing<Status> {
        val networkState = MutableLiveData<NetworkState>().apply { NetworkState.LOADED }
        val isRefreshing = MutableLiveData<Boolean>().apply { value = false }
        val boundaryCallback =
            TimelineBoundaryCallback(dao, kind, apiDir, token, networkState, isRefreshing)

        val dataSourceFactory = dao.getTimeline(kind.ordinal).map { it.asDomainModel() }
        val livePagedList = LivePagedListBuilder(dataSourceFactory, TIMELINE_PAGE_SIZE)
            .setBoundaryCallback(boundaryCallback)
            .build()

        return Listing(
            pagedList = livePagedList,
            networkState = networkState,
            isRefreshing = isRefreshing,
            refresh = { refresh(networkState, isRefreshing) },
            retry = { boundaryCallback.retryAllFailed() }
        )
    }

    private fun refresh(
        networkState: MutableLiveData<NetworkState>,
        isRefreshing: MutableLiveData<Boolean>
    ) {
        isRefreshing.postValue(true)
        CoroutineScope(context = Dispatchers.IO).launch {
            try {
                val response = kind.getter(apiDir, token, null, null)
                val status = response.body()?.map { it.asDatabaseModel(kind.ordinal) }
                    ?: throw IOException("response.body() is null") // todo レスポンスエラー処理

                dao.deleteAll()
                dao.insertAll(status)
                networkState.postValue(NetworkState.LOADED)
            } catch (ioException: IOException) {
                networkState.postValue(NetworkState.error(ioException.message ?: "unknown error"))
            } finally {
                isRefreshing.postValue(false)
            }
        }
    }

    companion object {
        const val TIMELINE_PAGE_SIZE = 20
    }
}