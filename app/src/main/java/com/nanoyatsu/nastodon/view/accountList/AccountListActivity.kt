package com.nanoyatsu.nastodon.view.accountList

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.data.api.endpoint.AccountListGetter
import com.nanoyatsu.nastodon.data.api.entity.Account
import kotlinx.android.synthetic.main.content_main.*

class AccountListActivity : AppCompatActivity() {
    enum class IntentKey { TITLE, GETTER }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)
//        setSupportActionBar(toolBar) // todo そのうち

        val listGetter = intent.getSerializableExtra(IntentKey.GETTER.name)
        if (listGetter is AccountListGetter) {
            val layoutManager = LinearLayoutManager(this@AccountListActivity, RecyclerView.VERTICAL, false)
            timeline_view.layoutManager = layoutManager

            val accounts = listGetter()
            val adapter =
                AccountsAdapter(
                    baseContext,
                    arrayListOf<Account>().also { it.addAll(accounts) })
            timeline_view.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }
}