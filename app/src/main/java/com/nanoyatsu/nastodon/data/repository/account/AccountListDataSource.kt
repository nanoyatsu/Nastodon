package com.nanoyatsu.nastodon.data.repository.account

import androidx.lifecycle.MutableLiveData
import androidx.paging.ItemKeyedDataSource
import com.nanoyatsu.nastodon.components.networkState.NetworkState
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiAccounts
import com.nanoyatsu.nastodon.data.api.entity.APIAccount
import com.nanoyatsu.nastodon.data.domain.Account
import com.nanoyatsu.nastodon.view.accountList.AccountListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class AccountListDataSource(
    private val kind: AccountListViewModel.Kind,
    private val apiDir: MastodonApiAccounts,
    private val token: String,
    private val accountId: String,
    private val networkState: MutableLiveData<NetworkState>,
    private val isRefreshing: MutableLiveData<Boolean>
) : ItemKeyedDataSource<String, Account>() {
    private var retry: (() -> Unit)? = null

    // 通信処理の共通部品
    private suspend fun <T : LoadCallback<Account>> tryLoad(
        callback: T, getter: suspend () -> Response<List<APIAccount>>, retry: (() -> Unit)
    ) {
        try {
            val response = getter()
            val statuses = response.body()?.map { it.asDomainModel() } ?: emptyList()

            this.retry = null
            networkState.postValue(NetworkState.LOADED)

            callback.onResult(statuses)
        } catch (ioException: IOException) {
            this.retry = retry
            networkState.postValue(NetworkState.error(ioException.message ?: "unknown error"))
        }
        // todo } catch (e: HttpException) {
    }

    override fun loadInitial(
        params: LoadInitialParams<String>, callback: LoadInitialCallback<Account>
    ) {
        isRefreshing.postValue(true)
        CoroutineScope(Dispatchers.IO).launch {
            tryLoad(callback,
                { kind.getter(apiDir, token, accountId, null, null) },
                { loadInitial(params, callback) })
            isRefreshing.postValue(false)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<Account>) {
        CoroutineScope(Dispatchers.IO).launch {
            tryLoad(callback,
                { kind.getter(apiDir, token, accountId, params.key, null) },
                { loadAfter(params, callback) })
        }
    }

    // なにもしない 未来方向のloadは実装しない
    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<Account>) {}

    override fun getKey(item: Account): String = item.id

    /**
     * 外部から要求する再取得処理
     */
    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.invoke()
    }

}