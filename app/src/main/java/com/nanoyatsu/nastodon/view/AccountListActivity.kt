package com.nanoyatsu.nastodon.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.presenter.AccountListGetter
import kotlinx.android.synthetic.main.content_main.*

class AccountListActivity : AppCompatActivity() {
    enum class IntentKey { TITLE, GETTER }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)
//        setSupportActionBar(toolBar) // todo そのうち

        val listGetter = intent.getSerializableExtra(IntentKey.GETTER.name)
        if (listGetter is AccountListGetter) {
            val accounts = listGetter()
            val adapter = AccountsAdapter(baseContext, accounts)
            adapter.notifyDataSetChanged()
            timelineView.adapter = adapter
        }
    }
}