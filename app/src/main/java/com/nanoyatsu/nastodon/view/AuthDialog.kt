package com.nanoyatsu.nastodon.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.model.Apps
import com.nanoyatsu.nastodon.presenter.MastodonApiManager
import kotlinx.android.synthetic.main.auth_dialog.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_dialog)

        sendButton.setOnClickListener { sendAuth() }
    }

    fun sendAuth(){
        val baseUrl = "https://qiitadon.com/"
        val api = MastodonApiManager(baseUrl).api
        val apps = api.getClientId()

        apps.enqueue(object : Callback<Apps> {
            override fun onResponse(call: Call<Apps>, response: Response<Apps>) {
                val authDir = baseUrl + "oauth/authorize" +
                        "?client_id=${response.body()?.client_id}" +
                        "&scope=read write follow" +
                        "&response_type=code" +
                        "&redirect_uri=urn:ietf:wg:oauth:2.0:oob"

                val uri = Uri.parse(authDir)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
            }
            override fun onFailure(call: Call<Apps>, t: Throwable) {
                t.printStackTrace()
                TODO(call.toString()) //To change body of created functions use File | Settings | File Templates.
            }
        })
    }
}