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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TootEditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_toot_edit)
        val pref = AuthPreferenceManager(this@TootEditActivity)
        if (pref.accessToken == "") finish()

        buttonSend.setOnClickListener { sendToot() }
    }

    private fun generateRequestApi(pref: AuthPreferenceManager): Call<Status> {
        return MastodonApiManager(pref.instanceUrl).api.postToot(
            authorization = pref.accessToken,
            status = statusContent.text.toString(),
            visibility = Visibility.unlisted.name
        )
    }

    private fun sendToot() {
        val pref = AuthPreferenceManager(this@TootEditActivity)
        val request = generateRequestApi(pref)

        request.enqueue(object : Callback<Status> {
            override fun onResponse(call: Call<Status>, response: Response<Status>) {
                Log.d(
                    this@TootEditActivity.javaClass.simpleName,
                    response.body()?.toString() ?: response.errorBody().toString()
                )
                // {"error": "アクセストークンは取り消されています"} 消してたらこうなる
                finish()
            }

            override fun onFailure(call: Call<Status>, t: Throwable) {
                t.printStackTrace()
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }
}