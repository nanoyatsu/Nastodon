package com.nanoyatsu.nastodon.data.repository.accountToots

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.nanoyatsu.nastodon.components.networkState.NetworkState
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiAccounts
import com.nanoyatsu.nastodon.data.domain.Status

class AccountTootsDataSourceFactory(
    private val apiDir: MastodonApiAccounts,
    private val token: String,
    private val accountId: String,
    private val networkState: MutableLiveData<NetworkState>,
    private val isRefreshing: MutableLiveData<Boolean>
) : DataSource.Factory<String, Status>() {
    var source: AccountTootsDataSource? = null
    override fun create(): DataSource<String, Status> {
        val dataSource =
            AccountTootsDataSource(apiDir, token, accountId, networkState, isRefreshing)
        source = dataSource
        return dataSource
    }
}