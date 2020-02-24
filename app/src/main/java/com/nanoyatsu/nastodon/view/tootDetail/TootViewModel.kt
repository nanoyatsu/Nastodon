package com.nanoyatsu.nastodon.view.tootDetail

import android.content.Intent
import android.net.Uri
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.data.domain.Account
import com.nanoyatsu.nastodon.data.domain.Status
import com.nanoyatsu.nastodon.data.repository.toot.TootRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class TootViewModel @Inject constructor(
    initToot: Status,
    val repo: TootRepository
) : ViewModel() {
    val vmJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + vmJob)

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

    private val _avatarEvent = MutableLiveData<Boolean>().apply { value = false }
    val avatarClickEvent: LiveData<Boolean> get() = _avatarEvent
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

    fun isMyToot() = repo.isMyToot(toot.value!!.account.id)
    fun toggleFolding() = run { isFolding.value = isFolding.value?.not() }

    fun onAvatarClicked() = run { if (!_avatarEvent.value!!) _avatarEvent.postValue(true) }
    fun onAvatarClickFinished() = run { _avatarEvent.postValue(false) }
    fun onReplyClicked() = run { if (!_replyEvent.value!!) _replyEvent.postValue(true) }
    fun onReplyClickFinished() = run { _replyEvent.postValue(false) }
    fun onReblogClicked() = run { if (!_reblogEvent.value!!) _reblogEvent.postValue(true) }
    private fun onReblogFinished() = run { _reblogEvent.postValue(false) }
    fun onFavouriteClicked() = run { if (!_favouriteEvent.value!!) _favouriteEvent.postValue(true) }
    private fun onFavouriteFinished() = run { _favouriteEvent.postValue(false) }
    fun onTimeClicked() = run { if (!_timeClickEvent.value!!) _timeClickEvent.postValue(true) }
    fun onTimeClickFinished() = run { _timeClickEvent.postValue(false) }
    fun onMoreClicked() = run { if (!_moreClickEvent.value!!) _moreClickEvent.postValue(true) }
    fun onMoreClickFinished() = run { _moreClickEvent.postValue(false) }

    fun doReblog() = ioScope.launch {
        repo.doReblog(toot.value!!.id, reblogged.value!!)
        onReblogFinished()
    }

    fun doFav() = ioScope.launch {
        repo.doFav(toot.value!!.id, favourited.value!!)
        onFavouriteFinished()
    }

    fun doPin() = ioScope.launch { repo.doPin(toot.value!!.id, toot.value!!.pinned) }

    fun doDelete() = ioScope.launch { repo.doDelete(toot.value!!.id) }

    val shareIntent: Intent
        get() {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                val plainText = HtmlCompat
                    .fromHtml(toot.value!!.content, HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_DIV)
                    .toString()
                val replaced = plainText.replace("""(\n)+$""".toRegex(), "")
                putExtra(Intent.EXTRA_TEXT, "${replaced}\n${toot.value!!.uri}")
                type = "text/plain"
            }
            return Intent.createChooser(sendIntent, null).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }

    val tootUriIntent: Intent
        get() {
            val uri = Uri.parse(toot.value!!.uri)
            return Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
}