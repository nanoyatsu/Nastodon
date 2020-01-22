package com.nanoyatsu.nastodon.view.tootEdit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.entity.Status
import com.nanoyatsu.nastodon.data.api.entity.Visibility
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import java.util.*


class TootEditViewModel(
    replyTo: Status?,
    private val auth: AuthInfo,
    private val apiManager: MastodonApiManager
) : ViewModel() {
    val visibilitySpinnerEntries = Visibility.values().map { it.label }

    private val _liveReplyTo = MutableLiveData<Status?>().apply { value = replyTo }
    val liveReplyTo: LiveData<Status?> get() = _liveReplyTo

    // 双方向binding対象
    val isContentWarning =
        MutableLiveData<Boolean>().apply { value = replyTo?.spoilerText?.isNotEmpty() ?: false }
    val cwContent = MutableLiveData<String>().apply { value = replyTo?.spoilerText ?: "" }
    val sendContent = MutableLiveData<String>().apply { value = "" }
    val visibilityIdx = MutableLiveData<Int>()
        .apply {
            val vis = replyTo?.visibility?.toUpperCase(Locale.ROOT)
            value = if (vis == null) Visibility.PUBLIC.ordinal else Visibility.valueOf(vis).ordinal
        }

    private val _tootSendEvent = MutableLiveData<Boolean>().apply { value = false }
    val tootSendEvent: LiveData<Boolean>
        get() = _tootSendEvent

    fun onTootSendClicked() = run { _tootSendEvent.value = true }
    fun onTootSendFinished() = run { _tootSendEvent.value = false }
    fun onReplyToClearClicked() = run { _liveReplyTo.value = null }

    fun sendToot(messenger: (String) -> Unit) {
        CoroutineScope(context = Dispatchers.Main).launch {
            try {
                val res = returnArgSetPostToot().invoke()
                Log.d(
                    this.javaClass.simpleName,
                    res.body()?.toString() ?: res.errorBody().toString()
                )
                // {"error": "アクセストークンは取り消されています"} 消してたらこうなる
                messenger("トゥートを投稿しました")
//                true
            } catch (e: HttpException) {
                e.printStackTrace()
                messenger("トゥートの送信に失敗しました")
//                false
            }
        }
    }

    private fun returnArgSetPostToot(): suspend () -> Response<Status> {
        return suspend {
            apiManager.statuses.postToot(
                authorization = auth.accessToken,
                status = sendContent.value!!,
                inReplyToId = liveReplyTo.value?.id,
                spoilerText = if (cwContent.value == "") null else cwContent.value,
                visibility = Visibility.values()[this.visibilityIdx.value!!].name.toLowerCase(Locale.ROOT)
            )
        }
    }

}