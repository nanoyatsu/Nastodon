package com.nanoyatsu.nastodon.data.repository.timeline

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.nanoyatsu.nastodon.components.networkState.NetworkState
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.entity.APIStatus
import com.nanoyatsu.nastodon.data.database.dao.TimelineDao
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.data.domain.Status
import com.nanoyatsu.nastodon.view.timeline.TimelineViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

// review NoticeBoundaryCallbackとほぼ同じ たぶんKindの抽象化が出来たら型引数で解決できる
class TimelineBoundaryCallback @Inject constructor(
    private val dao: TimelineDao,
    private val kind: TimelineViewModel.Kind,
    apiManager: MastodonApiManager,
    auth: AuthInfo,
    val networkState: MutableLiveData<NetworkState>,
    val isRefreshing: MutableLiveData<Boolean>
) : PagedList.BoundaryCallback<Status>() {
    val apiDir = apiManager.timelines
    val token = auth.accessToken

    private var retry: (() -> Unit)? = null

    // 通信処理の共通部品
    private suspend fun tryLoad(
        getter: suspend () -> Response<List<APIStatus>>,
        retry: () -> Unit
    ) {
        try {
            val response = getter()
            val statuses = response.body() ?: emptyList()

            this.retry = null
            networkState.postValue(NetworkState.LOADED)

            dao.insertAll(statuses.map { it.asDatabaseModel(kind.ordinal) })
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

    override fun onItemAtEndLoaded(itemAtEnd: Status) {
        CoroutineScope(Dispatchers.IO).launch {
            tryLoad({ kind.getter(apiDir, token, itemAtEnd.id, null) },
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