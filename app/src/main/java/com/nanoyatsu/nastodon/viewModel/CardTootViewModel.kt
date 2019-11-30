package com.nanoyatsu.nastodon.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.nanoyatsu.nastodon.data.entity.AuthInfo
import com.nanoyatsu.nastodon.model.Status
import com.nanoyatsu.nastodon.presenter.MastodonApiManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response

class CardTootViewModel(
    initToot: Status,
    private val auth: AuthInfo,
    apiManager: MastodonApiManager
) {
    val vmJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.Main + vmJob)
    private val apiStatuses = apiManager.statuses
    private val apiFavourites = apiManager.favourites

    private val _toot = MutableLiveData<Status>()
    val toot: LiveData<Status>
        get() = _toot
    private val _reblogEvent = MutableLiveData<Boolean>()
    val reblogEvent: LiveData<Boolean>
        get() = _reblogEvent
    private val _favouriteEvent = MutableLiveData<Boolean>()
    val favouriteEvent: LiveData<Boolean>
        get() = _favouriteEvent

    init {
        _toot.value = initToot
        _reblogEvent.value = false
        _favouriteEvent.value = false
    }

    // Toot:Statusのうち、変動する値
    val repliesCount = Transformations.map(toot) { it.repliesCount }
    val reblogsCount = Transformations.map(toot) { it.reblogsCount }
    val favouritesCount = Transformations.map(toot) { it.favouritesCount }
    val reblogged = Transformations.map(toot) { it.reblogged }
    val favourited = Transformations.map(toot) { it.favourited }

    fun onReblogClicked() = run { _reblogEvent.value = true }
    private fun onReblogFinished() = run { _reblogEvent.value = false }
    fun onFavouriteClicked() = run { _favouriteEvent.value = true }
    private fun onFavouriteFinished() = run { _favouriteEvent.value = false }

    fun doReblog() {
        val api = if (reblogged.value!!) apiStatuses::unReblog else apiStatuses::reblog
        doStatusApi(suspend { api(auth.accessToken, toot.value!!.id) })
        onReblogFinished()
    }

    fun doFav() {
        val api = if (favourited.value!!) apiFavourites::unFavourite else apiFavourites::favourite
        doStatusApi(suspend { api(auth.accessToken, toot.value!!.id) })
        onFavouriteFinished()
    }

    private fun doStatusApi(api: suspend () -> Response<Status>) {
        ioScope.launch {
            try {
                val res = api()
                if (res.body() == null) {
                    // todo res.errorBody()（JSONパース失敗かと思うので、ここに来る時はたぶん実装ミス）（下位互換切り仕様変更も無いと思いたい）
                }
                _toot.value = res.body()!!
            } catch (e: Exception) {
                // todo エラー表示
            }
        }
    }
}