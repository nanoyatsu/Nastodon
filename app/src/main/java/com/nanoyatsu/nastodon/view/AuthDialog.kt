package com.nanoyatsu.nastodon.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.model.Apps
import com.nanoyatsu.nastodon.model.AuthPreferenceManager
import com.nanoyatsu.nastodon.presenter.MastodonApiManager
import kotlinx.android.synthetic.main.auth_dialog.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 認証情報取得
 * インスタンスURL設定、アクセストークン取得
 * todo dialogとかactivityとか定めたら名前をかえる
 */
class AuthDialog : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_dialog)

        sendButton.setOnClickListener { sendAuth() }
        fromUri()
    }

    private fun fromUri() {
        val action = intent.action
        if (Intent.ACTION_VIEW == action) {
            val uri = intent.data
            val pref = AuthPreferenceManager(this)
            pref.accessToken = uri?.getQueryParameter("code") ?: ""
            finish()
        }
    }

    private fun sendAuth() {
        val pref = AuthPreferenceManager(this@AuthDialog)
        val baseUrl = "https://${instanceUrl.text}/"
        pref.instanceUrl = baseUrl

        val api = MastodonApiManager(baseUrl).api
        val apps = api.getClientId()

        apps.enqueue(object : Callback<Apps> {
            override fun onResponse(call: Call<Apps>, response: Response<Apps>) {
                pref.clientId = response.body()?.client_id ?: ""
                pref.clientSecret = response.body()?.client_secret ?: ""

                val authPath = baseUrl + "oauth/authorize" +
                        "?client_id=${response.body()?.client_id}" +
                        "&redirect_uri=${response.body()?.redirect_uri}" +
                        "&response_type=code" +
                        "&scope=${"read write follow"}"
                val uri = Uri.parse(authPath)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                this@AuthDialog.startActivity(intent)
            }

            override fun onFailure(call: Call<Apps>, t: Throwable) {
                t.printStackTrace()
                // TODO(call.toString()) // 失敗しました的なこと
            }
        })
    }
}