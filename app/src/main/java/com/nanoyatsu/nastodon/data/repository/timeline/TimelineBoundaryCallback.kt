package com.nanoyatsu.nastodon.data.repository.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.nanoyatsu.nastodon.components.networkState.NetworkState
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiTimelines
import com.nanoyatsu.nastodon.data.api.entity.APIStatus
import com.nanoyatsu.nastodon.data.database.dao.TimelineDao
import com.nanoyatsu.nastodon.data.entity.Status
import com.nanoyatsu.nastodon.view.timeline.TimelineViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class TimelineBoundaryCallback(
    private val timelineDao: TimelineDao,
    private val timelineKind: TimelineViewModel.Kind,
    private val apiDir: MastodonApiTimelines,
    private val token: String
) : PagedList.BoundaryCallback<Status>() {
    private var retry: (() -> Unit)? = null

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState
    private val _isInitialising = MutableLiveData<Boolean>()
    val isInitialising: LiveData<Boolean>
        get() = _isInitialising

    // 通信処理の共通部品
    private suspend fun tryLoad(
        getter: suspend () -> Response<List<APIStatus>>,
        retry: () -> Unit
    ) {
        try {
            val response = getter()
            val statuses = response.body() ?: emptyList()

            this.retry = null
            _networkState.postValue(NetworkState.LOADED)

            timelineDao.insertAll(statuses.map { it.asDatabaseModel(timelineKind.ordinal) })
        } catch (ioException: IOException) {
            this.retry = retry
            _networkState.postValue(NetworkState.error(ioException.message ?: "unknown error"))
        }
        // todo } catch (e: HttpException) {
    }

    override fun onZeroItemsLoaded() {
        _isInitialising.postValue(true)
        CoroutineScope(Dispatchers.IO).launch {
            tryLoad({ timelineKind.getter(apiDir, token, null, null) },
                { onZeroItemsLoaded() })

            _isInitialising.postValue(false)
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: Status) {
        CoroutineScope(Dispatchers.IO).launch {
            tryLoad({ timelineKind.getter(apiDir, token, itemAtEnd.id, null) },
                { onItemAtEndLoaded(itemAtEnd) })
        }
    }

    // なにもしない 未来方向のloadは(いまのところ)実装しない
    override fun onItemAtFrontLoaded(itemAtFront: Status) {}

    /**
     * 外部から要求する再取得処理
     */
    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.invoke()
    }
}