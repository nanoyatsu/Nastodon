package com.nanoyatsu.nastodon.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.model.AuthPreferenceManager
import com.nanoyatsu.nastodon.presenter.MastodonApi
import com.nanoyatsu.nastodon.presenter.MastodonApiManager
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

/**
 * 認証情報取得
 * インスタンスURL設定、アクセストークン取得
 */
class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        sendButton.setOnClickListener { sendAuth() }
        if (intent.action == Intent.ACTION_VIEW)
            fromUri()
    }

    private fun fromUri() {
        val uri = intent.data
        val pref = AuthPreferenceManager(this)

        val code = uri?.getQueryParameter("code") ?: ""
        val api = MastodonApiManager(pref.instanceUrl).api

        CoroutineScope(context = Dispatchers.Main).launch {
            try {
                val res = api.getAccessToken(
                    MastodonApi.TokenBody(
                        client_id = pref.clientId,
                        client_secret = pref.clientSecret,
                        code = code
                    )
                )

                pref.accessToken = res.body()?.accessToken ?: ""
                pref.accessTokenCreatedAt = res.body()?.createdAt ?: 0
                finish()
            } catch (e: HttpException) {
                e.printStackTrace()
                finish()
            }
        }
    }

    private fun sendAuth() {
        val pref = AuthPreferenceManager(this@AuthActivity)
        val baseUrl = "https://${instanceUrl.text}/"
        pref.instanceUrl = baseUrl

        val api = MastodonApiManager(baseUrl).api
        CoroutineScope(context = Dispatchers.Main).launch {
            try {
                val res = api.getClientId()

                pref.clientId = res.body()?.client_id ?: ""
                pref.clientSecret = res.body()?.client_secret ?: ""

                val authPath = baseUrl + "oauth/authorize" +
                        "?client_id=${res.body()?.client_id}" +
                        "&redirect_uri=${res.body()?.redirect_uri}" +
                        "&response_type=code" +
                        "&scope=${"read write follow"}"
                val uri = Uri.parse(authPath)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                this@AuthActivity.startActivity(intent)

            } catch (e: HttpException) {
                e.printStackTrace()
                // TODO(call.toString()) // 失敗しました的なこと
            }
        }
    }
}