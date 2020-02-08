package com.nanoyatsu.nastodon.view.accountDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.data.domain.Account
import com.nanoyatsu.nastodon.data.domain.Relationship
import com.nanoyatsu.nastodon.data.repository.account.AccountRepository
import kotlinx.coroutines.*

class AccountViewModel(val account: Account, val repo: AccountRepository) : ViewModel() {
    val vmJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.Main + vmJob)

    private val repoResult by lazy { repo.posts() }
    val toots get() = repoResult.pagedList
    val networkState get() = repoResult.networkState
    val isInitialising get() = repoResult.isRefreshing
    private val refresh get() = repoResult.refresh
    private val retry get() = repoResult.retry

    private val relationship by lazy {
        MutableLiveData<Relationship?>().also {
            it.value = null
            ioScope.launch { it.value = repo.relationship() }
        }
    }
    val following by lazy { Transformations.map(relationship) { it?.following } }
    val muting by lazy { Transformations.map(relationship) { it?.muting } }
    val blocking by lazy { Transformations.map(relationship) { it?.blocking } }

    private val _avatarClickEvent = MutableLiveData<Boolean>().apply { value = false }
    val avatarClickEvent: LiveData<Boolean> get() = _avatarClickEvent
    private val _followEvent = MutableLiveData<Boolean>().apply { value = false }
    val followEvent: LiveData<Boolean> get() = _followEvent
    private val _followingsEvent = MutableLiveData<Boolean>().apply { value = false }
    val followingsEvent: LiveData<Boolean> get() = _followingsEvent
    private val _followersEvent = MutableLiveData<Boolean>().apply { value = false }
    val followersEvent: LiveData<Boolean> get() = _followersEvent

    init {
        ioScope.launch {

        }
    }

    fun refresh() = refresh.invoke()
    fun retry() = retry.invoke()

    fun onAvatarClicked() = run { _avatarClickEvent.value = true }
    fun onAvatarClickFinished() = run { _avatarClickEvent.value = false }
    fun onFollowClicked() = run { _followEvent.value = true }
    fun onFollowClickFinished() = run { _followEvent.value = false }
    fun onFollowingsClicked() = run { _followingsEvent.value = true }
    fun onFollowingsClickFinished() = run { _followingsEvent.value = false }
    fun onFollowersClicked() = run { _followersEvent.value = true }
    fun onFollowersClickFinished() = run { _followersEvent.value = false }

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