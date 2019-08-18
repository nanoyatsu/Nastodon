package com.nanoyatsu.nastodon.view

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.model.AuthPreferenceManager
import com.nanoyatsu.nastodon.model.Status
import com.nanoyatsu.nastodon.model.Visibility
import com.nanoyatsu.nastodon.presenter.MastodonApi
import com.nanoyatsu.nastodon.presenter.MastodonApiManager
import kotlinx.android.synthetic.main.activity_toot_edit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class TootEditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_toot_edit)
        val pref = AuthPreferenceManager(this@TootEditActivity)
        if (pref.accessToken == "") finish()

        buttonSend.setOnClickListener { sendToot() }
    }

    private fun sendToot() {
        val pref = AuthPreferenceManager(this@TootEditActivity)

        CoroutineScope(context = Dispatchers.Main).launch {
            try {
                val res = MastodonApiManager(pref.instanceUrl).api.postToot(
                    authorization = pref.accessToken,
                    status = statusContent.text.toString(),
                    visibility = Visibility.unlisted.name
                )
                Log.d(
                    this@TootEditActivity.javaClass.simpleName,
                    res.body()?.toString() ?: res.errorBody().toString()
                )
                // {"error": "アクセストークンは取り消されています"} 消してたらこうなる
                finish()
            } catch (e: HttpException) {
                e.printStackTrace()
            }
        }
    }
}