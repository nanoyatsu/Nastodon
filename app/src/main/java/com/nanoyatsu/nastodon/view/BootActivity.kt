package com.nanoyatsu.nastodon.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.model.Apps
import com.nanoyatsu.nastodon.model.AuthPreferenceManager
import com.nanoyatsu.nastodon.presenter.MastodonApiManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException

class BootActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boot)

        // 画面つくる→即抜けでいい
        CoroutineScope(context = Dispatchers.Default).launch {
            val pref = AuthPreferenceManager(this@BootActivity)

            val transIntent =
                if (hasAuthInfo(pref) && verifyCredentials(pref)) {
                    Intent(this@BootActivity, MainActivity::class.java)
                } else {
                    Intent(this@BootActivity, AuthActivity::class.java)
                }
            startActivity(transIntent)
        }
    }

    private fun hasAuthInfo(pref: AuthPreferenceManager): Boolean {
        if (pref.instanceUrl == "")
            return false
        if (pref.accessToken == "")
            return false
        return true
    }

    private fun verifyCredentials(pref: AuthPreferenceManager): Boolean {
        val api = MastodonApiManager(pref.instanceUrl).api
        var result = false
        runBlocking {
            result = try {
                val res = api.verifyCredentials(pref.accessToken)
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