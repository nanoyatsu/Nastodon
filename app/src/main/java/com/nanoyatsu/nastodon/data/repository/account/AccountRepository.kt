package com.nanoyatsu.nastodon.data.repository.account

import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import com.nanoyatsu.nastodon.components.networkState.Listing
import com.nanoyatsu.nastodon.components.networkState.NetworkState
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.data.domain.Account
import com.nanoyatsu.nastodon.data.domain.Relationship
import com.nanoyatsu.nastodon.data.domain.Status
import com.nanoyatsu.nastodon.view.accountList.AccountListViewModel
import javax.inject.Inject

class AccountRepository @Inject constructor(apiManager: MastodonApiManager, auth: AuthInfo) {
    val apiDir = apiManager.accounts
    val token = auth.accessToken

    fun posts(accountId: String): Listing<Status> {
        val networkState = MutableLiveData<NetworkState>().apply { NetworkState.LOADED }
        val isRefreshing = MutableLiveData<Boolean>().apply { value = false }
        val factory =
            AccountTootsDataSourceFactory(apiDir, token, accountId, networkState, isRefreshing)
        val pagedList =
            LivePagedListBuilder<String, Status>(factory, ACCOUNT_STATUSES_PAGE_SIZE).build()

        return Listing(
            pagedList = pagedList,
            networkState = networkState,
            isRefreshing = isRefreshing,
            refresh = { factory.source?.invalidate() },
            retry = { factory.source?.retryAllFailed() }
        )
    }

    suspend fun relationship(accountId: String): Relationship? {
        // todo エラー処理
        val relationships = apiDir.getRelationships(token, accountId)
        return relationships.body()?.firstOrNull()?.asDomainModel()
    }

    suspend fun follow(accountId: String): Relationship? {
        val relationship = apiDir.follow(token, accountId)
        return relationship.body()?.asDomainModel()
    }

    suspend fun unFollow(accountId: String): Relationship? {
        val relationship = apiDir.unFollow(token, accountId)
        return relationship.body()?.asDomainModel()
    }

    fun accounts(accountId: String, kind: AccountListViewModel.Kind): Listing<Account> {
        val networkState = MutableLiveData<NetworkState>().apply { NetworkState.LOADED }
        val isRefreshing = MutableLiveData<Boolean>().apply { value = false }
        val factory =
            AccountListDataSourceFactory(kind, apiDir, token, accountId, networkState, isRefreshing)
        val pagedList =
            LivePagedListBuilder<String, Account>(factory, ACCOUNT_STATUSES_PAGE_SIZE).build()

        return Listing(
            pagedList = pagedList,
            networkState = networkState,
            isRefreshing = isRefreshing,
            refresh = { factory.source?.invalidate() },
            retry = { factory.source?.retryAllFailed() }
        )
    }

    companion object {
        const val ACCOUNT_STATUSES_PAGE_SIZE = 20
    }

}