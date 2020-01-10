package com.nanoyatsu.nastodon.view.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApi
import com.nanoyatsu.nastodon.data.api.entity.Account
import com.nanoyatsu.nastodon.data.sharedPreference.AuthPreferenceManager
import com.nanoyatsu.nastodon.data.database.NastodonDataBase
import com.nanoyatsu.nastodon.data.database.dao.AuthInfoDao
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.view.NavHostActivity
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException


/**
 * 認証情報取得
 * インスタンスURL設定、アクセストークン取得
 * todo 認証中の一時情報として SharedPreferenceを使っている。シンプルに出来ないか検討（少なくともaccount系はすぐ消せる）
 */
class AuthActivity : AppCompatActivity() {

    private lateinit var authInfoDao: AuthInfoDao
    private lateinit var viewModel: AuthViewModel
    private lateinit var apiManager: MastodonApiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

//        viewModel = ViewModelProvider(this@AuthActivity).get(AuthViewModel::class.java)

        sendButton.setOnClickListener { sendAuth() }
        if (intent.action == Intent.ACTION_VIEW)
            fromUri()
    }

    private fun sendAuth() {
        val pref =
            AuthPreferenceManager(
                this@AuthActivity
            )
        pref.instanceUrl = instanceUrl.text.toString()
        apiManager = MastodonApiManager(instanceUrl.text.toString())

        val api = apiManager.apps
        CoroutineScope(context = Dispatchers.Main).launch {
            try {
                val res = api.getClientId()

                pref.clientId = res.body()?.client_id ?: ""
                pref.clientSecret = res.body()?.client_secret ?: ""

                val baseUrl = "https://${instanceUrl.text}/"
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

    private fun fromUri() {
        val uri = intent.data
        val pref =
            AuthPreferenceManager(
                this
            )
        authInfoDao = NastodonDataBase.getInstance().authInfoDao()
        apiManager = MastodonApiManager(pref.instanceUrl)

        CoroutineScope(context = Dispatchers.IO).launch {
            val auth = apiManager.api
            try {
                val res = auth.getAccessToken(
                    MastodonApi.TokenBody(
                        client_id = pref.clientId,
                        client_secret = pref.clientSecret,
                        code = uri?.getQueryParameter("code") ?: ""
                    )
                )

                pref.accessToken = res.body()?.accessToken ?: ""
                pref.accessTokenCreatedAt = res.body()?.createdAt ?: 0
                val account = getOwnAccount(pref)

                setAccountToPref(pref, account)
                insertDB(pref, authInfoDao)

                startActivity(Intent(this@AuthActivity, NavHostActivity::class.java))
                finish()
            } catch (e: HttpException) {
                e.printStackTrace()
                finish()
            }
        }
    }

    private fun getOwnAccount(pref: AuthPreferenceManager): Account? {
        val verify = apiManager.accounts::verifyCredentials
        return runBlocking { verify(pref.accessToken) }.body()
    }

    private fun setAccountToPref(pref: AuthPreferenceManager, account: Account?) {
        if (account !is Account)
            return

        pref.accountId = account.id
        pref.accountUsername = account.username
        pref.accountDisplayName = account.displayName
        pref.accountAvatar = account.avatar
        pref.accountHeader = account.header
    }

    private fun insertDB(pref: AuthPreferenceManager, dao: AuthInfoDao) {
        // ViewModelではgetClientIdから戻ってきた時に情報を保持できていない ここだけsharedPreferenceが残る
        dao.insert(
            AuthInfo(
                instanceUrl = pref.instanceUrl,
                clientId = pref.clientId,
                clientSecret = pref.clientSecret,
                accessToken = pref.accessToken,
                tokenCreatedAt = pref.accessTokenCreatedAt,
                accountId = pref.accountId,
                accountUsername = pref.accountUsername,
                accountDisplayName = pref.accountDisplayName,
                accountAvatar = pref.accountAvatar,
                accountHeader = pref.accountHeader
            )
        )
    }
}