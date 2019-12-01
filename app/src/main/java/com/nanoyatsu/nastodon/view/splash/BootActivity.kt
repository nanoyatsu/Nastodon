package com.nanoyatsu.nastodon.view.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.data.database.NastodonDataBase
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.data.api.entity.Apps
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.view.auth.AuthActivity
import com.nanoyatsu.nastodon.view.timeline.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import javax.inject.Inject

class BootActivity : AppCompatActivity() {

    @Inject
    lateinit var db: NastodonDataBase
    @Inject
    lateinit var apiManager: MastodonApiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as NastodonApplication).appComponent.inject(this@BootActivity)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boot)

        // 画面つくる→即抜けでいい
        CoroutineScope(context = Dispatchers.Default).launch {
            val auth = db.authInfoDao().getAll().firstOrNull()

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
        val api = apiManager.apps
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