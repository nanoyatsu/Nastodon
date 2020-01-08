package com.nanoyatsu.nastodon.view.notice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ItemKeyedDataSource
import com.nanoyatsu.nastodon.components.networkState.NetworkState
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiNotifications
import com.nanoyatsu.nastodon.data.api.entity.Notification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NoticeDataSource(
    private val noticeKind: NoticeViewModel.Kind,
    private val apiDir: MastodonApiNotifications,
    private val token: String
) : ItemKeyedDataSource<String, Notification>() {
    private var retry: (() -> Unit)? = null

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState
    private val _isInitialising = MutableLiveData<Boolean>()
    val isInitialising: LiveData<Boolean>
        get() = _isInitialising

    // 通信処理の共通部品
    private suspend fun <T : LoadCallback<Notification>> tryLoad(
        callback: T, getter: suspend () -> Response<List<Notification>>, retry: (() -> Unit)
    ) {
        try {
            val response = getter()
            val statuses = response.body() ?: emptyList()

            this.retry = null
            _networkState.postValue(NetworkState.LOADED)

            callback.onResult(statuses)
        } catch (ioException: IOException) {
            this.retry = retry
            _networkState.postValue(NetworkState.error(ioException.message ?: "unknown error"))
        }
        // todo } catch (e: HttpException) {
    }

    override fun loadInitial(
        params: LoadInitialParams<String>, callback: LoadInitialCallback<Notification>
    ) {
        _isInitialising.postValue(true)
        CoroutineScope(Dispatchers.IO).launch {
            tryLoad(
                callback,
                { noticeKind.getter(apiDir, token, null, null) },
                { loadInitial(params, callback) })

            _isInitialising.postValue(false)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<Notification>) {
        CoroutineScope(Dispatchers.IO).launch {
            tryLoad(callback,
                { noticeKind.getter(apiDir, token, params.key, null) },
                { loadAfter(params, callback) })
        }
    }

    // なにもしない 未来方向のloadは実装しない
    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<Notification>) {}

    override fun getKey(item: Notification): String = item.id

    /**
     * 外部から要求する再取得処理
     */
    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.invoke()
    }
}