package com.nanoyatsu.nastodon.view.tootEdit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.entity.Visibility
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.*

class TootEditViewModel(
    private val auth: AuthInfo,
    private val apiManager: MastodonApiManager
) : ViewModel() {
    // 双方向binding対象
    var isContentWarning = false//MutableLiveData<Boolean>().apply { value = false }
    val cwContent = MutableLiveData<String>().apply { value = "" }
    val sendContent = MutableLiveData<String>().apply { value = "" }

    private val _tootSendEvent = MutableLiveData<Boolean>().apply { value = false }
    val tootSendEvent: LiveData<Boolean>
        get() = _tootSendEvent

    fun onTootSendClicked() = run { _tootSendEvent.value = true }
    fun onTootSendFinished() = run { _tootSendEvent.value = false }

    fun sendToot(visibility: Visibility, messenger: (String) -> Unit) {
        CoroutineScope(context = Dispatchers.Main).launch {
            try {
                val res = apiManager.statuses.postToot(
                    authorization = auth.accessToken,
                    status = sendContent.value!!,
                    visibility = visibility.name.toLowerCase(Locale.ROOT)
                )
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

}