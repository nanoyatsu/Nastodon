package com.nanoyatsu.nastodon.data.repository.notice

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.nanoyatsu.nastodon.components.networkState.NetworkState
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiNotifications
import com.nanoyatsu.nastodon.data.api.entity.APINotification
import com.nanoyatsu.nastodon.data.database.dao.NoticeDao
import com.nanoyatsu.nastodon.data.entity.Notification
import com.nanoyatsu.nastodon.view.notice.NoticeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NoticeBoundaryCallback(
    private val dao: NoticeDao,
    private val kind: NoticeViewModel.Kind,
    private val apiDir: MastodonApiNotifications,
    private val token: String,
    private val networkState: MutableLiveData<NetworkState>,
    private val isRefreshing: MutableLiveData<Boolean>
) : PagedList.BoundaryCallback<Notification>() {
    private var retry: (() -> Unit)? = null

    // 通信処理の共通部品
    private suspend fun tryLoad(
        getter: suspend () -> Response<List<APINotification>>,
        retry: () -> Unit
    ) {
        try {
            val response = getter()
            val notices = response.body() ?: emptyList()

            this.retry = null
            networkState.postValue(NetworkState.LOADED)

            dao.insertAll(notices.map { it.asDatabaseModel(kind.ordinal) })
        } catch (ioException: IOException) {
            this.retry = retry
            networkState.postValue(NetworkState.error(ioException.message ?: "unknown error"))
        }
        // todo } catch (e: HttpException) {
    }

    override fun onZeroItemsLoaded() {
        isRefreshing.postValue(true)
        CoroutineScope(Dispatchers.IO).launch {
            tryLoad({ kind.getter(apiDir, token, null, null) },
                { onZeroItemsLoaded() })
            isRefreshing.postValue(false)
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: Notification) {
        CoroutineScope(Dispatchers.IO).launch {
            tryLoad({ kind.getter(apiDir, token, itemAtEnd.id, null) },
                { onItemAtEndLoaded(itemAtEnd) })
        }
    }

    // なにもしない 未来方向のloadは(いまのところ)実装しない
    override fun onItemAtFrontLoaded(itemAtFront: Notification) {}

    /**
     * 外部から要求する再取得処理
     */
    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.invoke()
    }
}