package com.nanoyatsu.nastodon.data.repository.account

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.nanoyatsu.nastodon.components.networkState.NetworkState
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiAccounts
import com.nanoyatsu.nastodon.data.domain.Account
import com.nanoyatsu.nastodon.view.accountList.AccountListViewModel

class AccountListDataSourceFactory(
    private val kind: AccountListViewModel.Kind,
    private val apiDir: MastodonApiAccounts,
    private val token: String,
    private val accountId: String,
    private val networkState: MutableLiveData<NetworkState>,
    private val isRefreshing: MutableLiveData<Boolean>
) : DataSource.Factory<String, Account>() {
    var source: AccountListDataSource? = null
    override fun create(): DataSource<String, Account> {
        val dataSource =
            AccountListDataSource(kind, apiDir, token, accountId, networkState, isRefreshing)
        source = dataSource
        return dataSource
    }
}