package com.nanoyatsu.nastodon.data.repository.notice

import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.components.networkState.Listing
import com.nanoyatsu.nastodon.components.networkState.NetworkState
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.database.dao.NoticeDao
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.data.domain.Notification
import com.nanoyatsu.nastodon.view.notice.NoticeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class NoticeRepository @Inject constructor(
    private val kind: NoticeViewModel.Kind,
    private val dao: NoticeDao,
    apiManager: MastodonApiManager,
    auth: AuthInfo
) {
    val apiDir = apiManager.notifications
    val token = auth.accessToken

    @Inject
    lateinit var boundaryCallback: NoticeBoundaryCallback

    init {
        val component = (NastodonApplication.appContext as NastodonApplication).appComponent
            .noticeComponent().create(kind)
        component.inject(this)
    }

    fun posts(): Listing<Notification> {
        val dataSourceFactory = dao.getNotice(kind.ordinal).map { it.asDomainModel() }
        val livePagedList = LivePagedListBuilder(dataSourceFactory, NOTICE_PAGE_SIZE)
            .setBoundaryCallback(boundaryCallback)
            .build()

        return Listing(
            pagedList = livePagedList,
            networkState = boundaryCallback.networkState,
            isRefreshing = boundaryCallback.isRefreshing,
            refresh = { refresh(boundaryCallback.networkState, boundaryCallback.isRefreshing) },
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
        const val NOTICE_PAGE_SIZE = 20
    }
}