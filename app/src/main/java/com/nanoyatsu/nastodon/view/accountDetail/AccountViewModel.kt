package com.nanoyatsu.nastodon.view.accountDetail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.data.domain.Account
import com.nanoyatsu.nastodon.data.domain.Relationship
import com.nanoyatsu.nastodon.data.repository.accountToots.AccountTootsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AccountViewModel(val account: Account, repo: AccountTootsRepository) : ViewModel() {
    val vmJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.Main + vmJob)

    private val repoResult = repo.posts()
    val toots = repoResult.pagedList
    val networkState = repoResult.networkState
    val isInitialising = repoResult.isRefreshing
    private val refresh = repoResult.refresh
    private val retry = repoResult.retry

    val relationship = MutableLiveData<Relationship?>().apply { value = null }

    init {
        ioScope.launch {
            relationship.value = repo.relationship()
            Log.d("core ha", "test")
        }
    }

    fun refresh() = refresh.invoke()
    fun retry() = retry.invoke()

    override fun onCleared() {
        vmJob.cancel()
    }
}