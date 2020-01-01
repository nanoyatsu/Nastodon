package com.nanoyatsu.nastodon.view.tootDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.entity.Status
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response

class TootViewModel(
    initToot: Status,
    private val auth: AuthInfo,
    apiManager: MastodonApiManager
) : ViewModel() {
    val vmJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.Main + vmJob)
    private val apiStatuses = apiManager.statuses
    private val apiFavourites = apiManager.favourites

    // 双方向binding対象
    val isFolding = MutableLiveData<Boolean>().apply { value = true }

    private val _toot = MutableLiveData<Status>().apply { value = initToot }
    val toot: LiveData<Status>
        get() = _toot
    private val _reblogEvent = MutableLiveData<Boolean>().apply { value = false }
    val reblogEvent: LiveData<Boolean>
        get() = _reblogEvent
    private val _favouriteEvent = MutableLiveData<Boolean>().apply { value = false }
    val favouriteEvent: LiveData<Boolean>
        get() = _favouriteEvent
    private val _timeClickEvent = MutableLiveData<Boolean>().apply { value = false }
    val timeClickEvent: LiveData<Boolean>
        get() = _timeClickEvent

    // Toot:Statusのうち、変動する値
    val repliesCount = Transformations.map(toot) { it.repliesCount }
    val reblogsCount = Transformations.map(toot) { it.reblogsCount }
    val favouritesCount = Transformations.map(toot) { it.favouritesCount }
    val reblogged = Transformations.map(toot) { it.reblogged }
    val favourited = Transformations.map(toot) { it.favourited }

    fun toggleFolding() = run { isFolding.value = isFolding.value?.not() }

    fun onReblogClicked() = run { _reblogEvent.value = true }
    private fun onReblogFinished() = run { _reblogEvent.value = false }
    fun onFavouriteClicked() = run { _favouriteEvent.value = true }
    private fun onFavouriteFinished() = run { _favouriteEvent.value = false }
    fun onTimeClicked() = run { _timeClickEvent.value = true }
    fun onTimeClickFinished() = run { _timeClickEvent.value = false }


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