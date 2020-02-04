package com.nanoyatsu.nastodon.view.accountDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.data.domain.Account
import com.nanoyatsu.nastodon.data.domain.Relationship
import com.nanoyatsu.nastodon.data.repository.accountToots.AccountTootsRepository
import kotlinx.coroutines.*

class AccountViewModel(val account: Account, val repo: AccountTootsRepository) : ViewModel() {
    val vmJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.Main + vmJob)

    private val repoResult = repo.posts()
    val toots = repoResult.pagedList
    val networkState = repoResult.networkState
    val isInitialising = repoResult.isRefreshing
    private val refresh = repoResult.refresh
    private val retry = repoResult.retry

    private val relationship = MutableLiveData<Relationship?>().apply { value = null }
    val following = Transformations.map(relationship) { it?.following }
    val muting = Transformations.map(relationship) { it?.muting }
    val blocking = Transformations.map(relationship) { it?.blocking }

    private val _followEvent = MutableLiveData<Boolean>().apply { value = false }
    val followEvent: LiveData<Boolean> get() = _followEvent

    init {
        ioScope.launch {
            relationship.value = repo.relationship()
        }
    }

    fun refresh() = refresh.invoke()
    fun retry() = retry.invoke()

    fun onFollowClicked() = run { _followEvent.value = true }
    fun onFollowClickFinished() = run { _followEvent.value = false }

    fun switchFollow() = runBlocking(Dispatchers.IO + vmJob) {
        val res = when (following.value) {
            true -> repo.unFollow()
            false -> repo.follow()
            else -> null
        }
        relationship.postValue(res)
    }

    override fun onCleared() {
        vmJob.cancel()
    }
}