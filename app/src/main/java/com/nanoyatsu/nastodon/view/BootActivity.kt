package com.nanoyatsu.nastodon.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.data.NastodonDataBase
import com.nanoyatsu.nastodon.data.dao.AuthInfoDao
import com.nanoyatsu.nastodon.data.entity.AuthInfo
import com.nanoyatsu.nastodon.model.Apps
import com.nanoyatsu.nastodon.presenter.MastodonApiManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException

class BootActivity : AppCompatActivity() {

    private lateinit var authInfoDao: AuthInfoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boot)

        // 画面つくる→即抜けでいい
        CoroutineScope(context = Dispatchers.Default).launch {
            authInfoDao = NastodonDataBase.getInstance().authInfoDao()
            // todo マルチアカウント考慮
            val auth = authInfoDao.getAll().firstOrNull()

            val transIntent =
                if (auth is AuthInfo && hasAuthInfo(auth) && verifyCredentials(auth)) {
                    Intent(this@BootActivity, MainActivity::class.java)
                } else {
                    Intent(this@BootActivity, AuthActivity::class.java)
                }
            startActivity(transIntent)
            finish()
        }
    }

    private fun hasAuthInfo(auth: AuthInfo): Boolean {
        if (auth.instanceUrl.isEmpty())
            return false
        if (auth.accessToken.isEmpty())
            return false
        return true
    }

    private fun verifyCredentials(auth: AuthInfo): Boolean {
        val api = MastodonApiManager(auth.instanceUrl).apps
        var result = false
        runBlocking {
            result = try {
                val res = api.verifyCredentials(auth.accessToken)
                val apps = res.body()
                // nameも一致するか確認
                apps is Apps && apps.name == getString(R.string.app_name)
            } catch (e: HttpException) {
                e.printStackTrace()
                false
            }
        }
        return result
    }
}