package com.nanoyatsu.nastodon.data.repository.notice

import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import com.nanoyatsu.nastodon.components.networkState.Listing
import com.nanoyatsu.nastodon.components.networkState.NetworkState
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiNotifications
import com.nanoyatsu.nastodon.data.database.dao.NoticeDao
import com.nanoyatsu.nastodon.data.entity.Notification
import com.nanoyatsu.nastodon.view.notice.NoticeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class NoticeRepository(
    private val kind: NoticeViewModel.Kind,
    private val dao: NoticeDao,
    private val apiDir: MastodonApiNotifications,
    private val token: String
) {
    companion object {
        const val NOTICE_PAGE_SIZE = 20
    }

    fun posts(): Listing<Notification> {
        val networkState = MutableLiveData<NetworkState>().apply { NetworkState.LOADED }
        val isRefreshing = MutableLiveData<Boolean>().apply { value = false }
        val boundaryCallback =
            NoticeBoundaryCallback(dao, kind, apiDir, token, networkState, isRefreshing)

        val dataSourceFactory = dao.getNotice(kind.ordinal).map { it.asDomainModel() }
        val livePagedList = LivePagedListBuilder(dataSourceFactory, NOTICE_PAGE_SIZE)
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
}