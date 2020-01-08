package com.nanoyatsu.nastodon.view.timeline


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ItemKeyedDataSource
import com.nanoyatsu.nastodon.components.networkState.NetworkState
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiTimelines
import com.nanoyatsu.nastodon.data.api.entity.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class TimelineDataSource(
    private val timelineKind: TimelineViewModel.Kind,
    private val apiDir: MastodonApiTimelines,
    private val token: String
) : ItemKeyedDataSource<String, Status>() {
    private var retry: (() -> Unit)? = null

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState
    private val _isInitialising = MutableLiveData<Boolean>()
    val isInitialising: LiveData<Boolean>
        get() = _isInitialising


    // todo 各loadの重複処理を抽象化
//    private suspend fun getByApi(getter: suspend () -> Response<List<Status>>): List<Status> {
//        return try {
//            val res = getter()
//            res.body() ?: listOf() // todo レスポンスが期待通りじゃないときの処理 res.errorBody()
//        } catch (e: HttpException) {
//            e.printStackTrace()
//            // todo 通信失敗のときの処理
//            listOf()
//        }
//    }

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<Status>
    ) {
        _isInitialising.postValue(true)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = timelineKind.getter(apiDir, token, null, null)
                val statuses = response.body() ?: emptyList()

                retry = null
                _networkState.postValue(NetworkState.LOADED)
                _isInitialising.postValue(false)

                callback.onResult(statuses)
            } catch (ioException: IOException) {
                retry = { loadInitial(params, callback) }
                val error = NetworkState.error(ioException.message ?: "unknown error")
                _networkState.postValue(error)
                _isInitialising.postValue(false)
            }
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<Status>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = timelineKind.getter(apiDir, token, params.key, null)
                val statuses = response.body() ?: emptyList()

                retry = null
                _networkState.postValue(NetworkState.LOADED)

                callback.onResult(statuses)
            } catch (ioException: IOException) {
                retry = { loadAfter(params, callback) }
                _networkState.postValue(NetworkState.error(ioException.message ?: "unknown err"))
            }
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<Status>) {
        // なにもしない 未来方向のloadは実装しない
    }

    override fun getKey(item: Status): String = item.id

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.invoke()
    }
}