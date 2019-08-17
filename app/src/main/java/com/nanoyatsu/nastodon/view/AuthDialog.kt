package com.nanoyatsu.nastodon.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.model.Apps
import com.nanoyatsu.nastodon.model.AuthPreferenceManager
import com.nanoyatsu.nastodon.model.Token
import com.nanoyatsu.nastodon.presenter.MastodonApi
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
        if (intent.action == Intent.ACTION_VIEW)
            fromUri()
    }

    private fun fromUri() {
        val uri = intent.data
        val pref = AuthPreferenceManager(this)

        val code = uri?.getQueryParameter("code") ?: ""
        val api = MastodonApiManager(pref.instanceUrl).api
        val token = api.getAccessToken(MastodonApi.TokenBody(
            client_id= pref.clientId,
            client_secret= pref.clientSecret,
            code = code
        ))

        token.enqueue(object : Callback<Token> {
            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                pref.accessToken = response.body()?.accessToken ?: ""
                pref.accessTokenCreatedAt = response.body()?.createdAt ?: 0
                finish()
            }

            override fun onFailure(call: Call<Token>, t: Throwable) {
                t.printStackTrace()
                finish()
                // TODO(call.toString()) // 失敗しました的なこと
            }
        })
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