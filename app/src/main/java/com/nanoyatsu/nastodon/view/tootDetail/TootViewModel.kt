package com.nanoyatsu.nastodon.view.tootDetail

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.data.domain.Account
import com.nanoyatsu.nastodon.data.domain.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

class TootViewModel @Inject constructor(
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

    private val _toot = MutableLiveData<Status>()
    val toot: LiveData<Status>
        get() = _toot
    private val _rebloggedBy = MutableLiveData<Account?>()
    val rebloggedBy: LiveData<Account?>
        get() = _rebloggedBy

    init {
        if (initToot.reblog == null) { // BTでないとき
            _toot.value = initToot
            _rebloggedBy.value = null
        } else { // BTのとき
            _toot.value = initToot.reblog
            _rebloggedBy.value = initToot.account
        }
    }

    private val _avatarClickEvent = MutableLiveData<Boolean>().apply { value = false }
    val avatarClickEvent: LiveData<Boolean> get() = _avatarClickEvent
    private val _replyEvent = MutableLiveData<Boolean>().apply { value = false }
    val replyEvent: LiveData<Boolean> get() = _replyEvent
    private val _reblogEvent = MutableLiveData<Boolean>().apply { value = false }
    val reblogEvent: LiveData<Boolean> get() = _reblogEvent
    private val _favouriteEvent = MutableLiveData<Boolean>().apply { value = false }
    val favouriteEvent: LiveData<Boolean> get() = _favouriteEvent
    private val _timeClickEvent = MutableLiveData<Boolean>().apply { value = false }
    val timeClickEvent: LiveData<Boolean> get() = _timeClickEvent
    private val _moreClickEvent = MutableLiveData<Boolean>().apply { value = false }
    val moreClickEvent: LiveData<Boolean> get() = _moreClickEvent

    // Toot:Statusのうち、変動する値
    val repliesCount = Transformations.map(toot) { it.repliesCount }
    val reblogsCount = Transformations.map(toot) { it.reblogsCount }
    val favouritesCount = Transformations.map(toot) { it.favouritesCount }
    val reblogged = Transformations.map(toot) { it.reblogged }
    val favourited = Transformations.map(toot) { it.favourited }

    fun isMyToot() = (auth.accountId == toot.value?.account?.id)
    fun toggleFolding() = run { isFolding.value = isFolding.value?.not() }

    fun onAvatarClicked() = run { if (!_avatarClickEvent.value!!) _avatarClickEvent.value = true }
    fun onAvatarClickFinished() = run { _avatarClickEvent.value = false }
    fun onReplyClicked() = run { if (!_replyEvent.value!!) _replyEvent.value = true }
    fun onReplyClickFinished() = run { _replyEvent.value = false }
    fun onReblogClicked() = run { if (!_reblogEvent.value!!) _reblogEvent.value = true }
    private fun onReblogFinished() = run { _reblogEvent.value = false }
    fun onFavouriteClicked() = run { if (!_favouriteEvent.value!!) _favouriteEvent.value = true }
    private fun onFavouriteFinished() = run { _favouriteEvent.value = false }
    fun onTimeClicked() = run { if (!_timeClickEvent.value!!) _timeClickEvent.value = true }
    fun onTimeClickFinished() = run { _timeClickEvent.value = false }
    fun onMoreClicked() = run { if (!_moreClickEvent.value!!) _moreClickEvent.value = true }
    fun onMoreClickFinished() = run { _moreClickEvent.value = false }

    fun doReblog() {
        val api = if (reblogged.value!!) apiStatuses::unReblog else apiStatuses::reblog
        doStatusApi(suspend { api(auth.accessToken, toot.value!!.id) }, ::onReblogFinished)
    }

    fun doFav() {
        val api = if (favourited.value!!) apiFavourites::unFavourite else apiFavourites::favourite
        doStatusApi(suspend { api(auth.accessToken, toot.value!!.id) }, ::onFavouriteFinished)
    }

    fun doPin() {
        val api = if (toot.value!!.pinned == true) apiStatuses::unPin else apiStatuses::pin
        doStatusApi(suspend { api(auth.accessToken, toot.value!!.id) }, {})
    }

    val tootUriIntent: Intent
        get() {
            val uri = Uri.parse(toot.value!!.uri)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            return intent
        }

    private fun doStatusApi(api: suspend () -> Response<Status>, onFinished: () -> Unit) {
        ioScope.launch {
            try {
                val res = api()
                if (res.body() == null) {
                    // todo res.errorBody()（JSONパース失敗かと思うので、ここに来る時はたぶん実装ミス）（下位互換切り仕様変更も無いと思いたい）
                }
                _toot.value = res.body()!!
            } catch (e: Exception) {
                // todo エラー表示
            } finally {
                onFinished()
            }
        }
    }
}