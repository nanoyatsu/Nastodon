package com.nanoyatsu.nastodon.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.model.Account
import kotlinx.android.synthetic.main.content_main.*

class AccountListActivity : AppCompatActivity() {
    enum class IntentKey { LIST }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)
//        setSupportActionBar(toolBar) // todo そのうち

        val accounts = intent.getParcelableArrayListExtra<Account>(IntentKey.LIST.name)
        if (accounts is ArrayList<Account>) {
            val adapter = AccountsAdapter(baseContext, accounts.toTypedArray())
            adapter.notifyDataSetChanged()
            timelineView.adapter = adapter
        }
    }
}