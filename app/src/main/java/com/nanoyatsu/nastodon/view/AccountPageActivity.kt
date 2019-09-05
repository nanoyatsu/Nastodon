package com.nanoyatsu.nastodon.view

import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.model.Account
import kotlinx.android.synthetic.main.activity_account_page.*

class AccountPageActivity : AppCompatActivity() {
    enum class IntentKey { ACCOUNT }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_page)

        val account = intent.getParcelableExtra<Account>(IntentKey.ACCOUNT.name)
        Glide.with(this@AccountPageActivity).load(account.headerStatic).into(header)
        Glide.with(this@AccountPageActivity).load(account.avatarStatic).circleCrop().into(avatar)
        displayName.text = account.displayName
        username.text = account.username
        note.text = Html.fromHtml(account.note, Html.FROM_HTML_MODE_COMPACT)


    }
}