package com.nanoyatsu.nastodon.data.repository.accountToots

import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import com.nanoyatsu.nastodon.components.networkState.Listing
import com.nanoyatsu.nastodon.components.networkState.NetworkState
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiAccounts
import com.nanoyatsu.nastodon.data.domain.Relationship
import com.nanoyatsu.nastodon.data.domain.Status

class AccountTootsRepository(
    private val apiDir: MastodonApiAccounts,
    private val token: String,
    private val accountId: String
) {
    companion object {
        const val ACCOUNT_STATUSES_PAGE_SIZE = 20
    }

    fun posts(): Listing<Status> {
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

    suspend fun relationship(): Relationship? {
        // todo エラー処理
        val relationships = apiDir.getRelationships(token, accountId)
        return relationships.body()?.firstOrNull()?.asDomainModel()
    }
}