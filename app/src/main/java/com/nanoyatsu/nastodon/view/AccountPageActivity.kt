package com.nanoyatsu.nastodon.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.model.Account

class AccountPageActivity : AppCompatActivity() {
    enum class IntentKey { ACCOUNT }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_page)

        val account = intent.getParcelableExtra<Account>(IntentKey.ACCOUNT.name)

        return
    }
}