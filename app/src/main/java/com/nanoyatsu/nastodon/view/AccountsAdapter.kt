package com.nanoyatsu.nastodon.view

import android.content.Context
import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.model.Account

class AccountsAdapter(
    context: Context,
    private val accounts: Array<Account>
) :
    ArrayAdapter<Account>(context, R.layout.card_toot, accounts) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val inflater: LayoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val thisView = convertView ?: inflater.inflate(R.layout.card_account, parent, false)


        thisView.findViewById<TextView>(R.id.displayName)?.text = accounts[position].displayName
        thisView.findViewById<TextView>(R.id.username)?.text = accounts[position].username
        thisView.findViewById<TextView>(R.id.note)?.text =
            Html.fromHtml(accounts[position].note, Html.FROM_HTML_MODE_COMPACT)

        val avatar = thisView.findViewById<ImageView>(R.id.accountAvatar)
        Glide.with(this.context).load(accounts[position].avatarStatic).circleCrop().into(avatar)

        avatar.setOnClickListener { transAccountPage(it, accounts[position]) }

        return thisView
    }

    private fun transAccountPage(v: View, account: Account) {
        val intent = Intent(context, AccountPageActivity::class.java).also {
            it.putExtra(AccountPageActivity.IntentKey.ACCOUNT.name, account)
        }
        v.context.startActivity(intent)
    }
}