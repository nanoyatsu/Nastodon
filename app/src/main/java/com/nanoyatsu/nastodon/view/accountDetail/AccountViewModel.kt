package com.nanoyatsu.nastodon.view.accountDetail

import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.data.domain.Account
import com.nanoyatsu.nastodon.data.repository.accountToots.AccountTootsRepository

class AccountViewModel(val account: Account, repo: AccountTootsRepository) : ViewModel() {
    private val repoResult = repo.posts()
    val toots = repoResult.pagedList
    val networkState = repoResult.networkState
    val isInitialising = repoResult.isRefreshing
    private val refresh = repoResult.refresh
    private val retry = repoResult.retry

    fun refresh() = refresh.invoke()
    fun retry() = retry.invoke()
}