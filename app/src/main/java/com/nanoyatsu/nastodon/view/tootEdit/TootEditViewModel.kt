package com.nanoyatsu.nastodon.view.tootEdit

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.components.ContentSchemeParser
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.entity.APIStatus
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.data.domain.Attachment
import com.nanoyatsu.nastodon.data.domain.Status
import com.nanoyatsu.nastodon.data.domain.Visibility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import java.util.*
import javax.inject.Inject


// todo リポジトリ実装
class TootEditViewModel @Inject constructor(
    replyTo: Status?,
    private val auth: AuthInfo,
    private val apiManager: MastodonApiManager
) : ViewModel() {
    val visibilitySpinnerEntries = Visibility.values().map { it.label }

    private val _liveReplyTo = MutableLiveData<Status?>().apply { value = replyTo }
    val liveReplyTo: LiveData<Status?> get() = _liveReplyTo
    // とりあえず１つ対応（ todo 最大４）
    private val _attachment = MutableLiveData<Attachment?>().apply { value = null }
    val attachment: LiveData<Attachment?> get() = _attachment

    // 双方向binding対象
    val isContentWarning =
        MutableLiveData<Boolean>().apply { value = replyTo?.spoilerText?.isNotEmpty() ?: false }
    val cwContent = MutableLiveData<String>().apply { value = replyTo?.spoilerText ?: "" }
    val sendContent = MutableLiveData<String>().apply { value = "" }
    val visibilityIdx = MutableLiveData<Int>()
        .apply { value = replyTo?.visibility?.ordinal ?: Visibility.PUBLIC.ordinal }

    private val _tootSendEvent = MutableLiveData<Boolean>().apply { value = false }
    val tootSendEvent: LiveData<Boolean>
        get() = _tootSendEvent
    private val _mediaAddEvent = MutableLiveData<Boolean>().apply { value = false }
    val mediaAddEvent: LiveData<Boolean>
        get() = _mediaAddEvent

    fun onTootSendClicked() = run { _tootSendEvent.postValue(true) }
    fun onTootSendFinished() = run { _tootSendEvent.postValue(false) }
    fun onMediaAddClicked() = run { _mediaAddEvent.postValue(true) }
    fun onMediaAddFinished() = run { _mediaAddEvent.postValue(false) }
    fun onReplyToClearClicked() = run { _liveReplyTo.postValue(null) }

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

    private fun returnArgSetPostToot(): suspend () -> Response<APIStatus> {
        return suspend {
            apiManager.statuses.postToot(
                authorization = auth.accessToken,
                status = sendContent.value!!,
                inReplyToId = liveReplyTo.value?.id,
                spoilerText = if (cwContent.value == "") null else cwContent.value,
                visibility = Visibility.values()[this.visibilityIdx.value!!].name.toLowerCase(Locale.ROOT),
                mediaIds = *arrayOf(_attachment.value?.id)
            )
        }
    }

    fun uploadAttachment(uri: Uri) {
        val path = ContentSchemeParser.getPathFromUri(NastodonApplication.appContext, uri) ?: return
        val file = File(path)
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
        CoroutineScope(context = Dispatchers.IO).launch {
            try {
                val apiDir = apiManager.media
                val res = apiDir.media(auth.accessToken, part)
                _attachment.postValue(res.body()?.asDomainModel())
                Log.d("tuusin", "sita")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("tuusin", "eratta")
            }
        }
    }
}