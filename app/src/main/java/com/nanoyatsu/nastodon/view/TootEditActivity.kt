package com.nanoyatsu.nastodon.view

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.data.NastodonDataBase
import com.nanoyatsu.nastodon.data.dao.AuthInfoDao
import com.nanoyatsu.nastodon.data.entity.AuthInfo
import com.nanoyatsu.nastodon.model.Visibility
import com.nanoyatsu.nastodon.presenter.MastodonApiManager
import kotlinx.android.synthetic.main.activity_toot_edit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException

class TootEditActivity : AppCompatActivity() {

    private lateinit var authInfoDao: AuthInfoDao
    private lateinit var auth: AuthInfo
    private lateinit var apiManager: MastodonApiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_toot_edit)
        authInfoDao = NastodonDataBase.getInstance().authInfoDao()
        // todo マルチアカウント考慮
        runBlocking(context = Dispatchers.IO) { auth = authInfoDao.getAll().first() }
        if (auth.instanceUrl == "") finish() // todo 認証に行く
        apiManager = MastodonApiManager(auth.instanceUrl)

        val adapter = ArrayAdapter(
            this@TootEditActivity,
            R.layout.support_simple_spinner_dropdown_item,
            Visibility.values().map { it.label }
        )
        visibilitySpinner.adapter = adapter

        buttonSend.setOnClickListener { sendToot() }
    }

    private fun sendToot() {
        val paramVisibility = Visibility.values()[visibilitySpinner.selectedItemPosition] // ちょっと型安全を失ってる

        CoroutineScope(context = Dispatchers.Main).launch {
            try {
                val res = apiManager.statuses.postToot(
                    authorization = auth.accessToken,
                    status = note.text.toString(),
                    visibility = paramVisibility.name.toLowerCase()
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