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
import com.nanoyatsu.nastodon.model.Status

class TimelineAdapter(context: Context, resource: Int, private val toots: Array<Status>) :
    ArrayAdapter<Status>(context, resource, toots) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val inflater: LayoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val thisView = convertView ?: inflater.inflate(R.layout.toot_card, parent, false)

        thisView.findViewById<TextView>(R.id.displayName)?.text = toots[position].account.displayName
        thisView.findViewById<TextView>(R.id.username)?.text = toots[position].account.username
        thisView.findViewById<TextView>(R.id.statusContent)?.text =
            Html.fromHtml(toots[position].content, Html.FROM_HTML_MODE_COMPACT)

        val avatar = thisView.findViewById<ImageView>(R.id.accountAvatar)
        Glide.with(this.context).load(toots[position].account.avatarStatic).circleCrop().into(avatar)

        avatar.setOnClickListener { transAccountPage(it, toots[position].account) }

        return thisView
    }

    private fun transAccountPage(v: View, account: Account) {
        val intent = Intent(context, AccountPageActivity::class.java).also {
            it.putExtra(AccountPageActivity.IntentKey.ACCOUNT.name, account)
        }
        v.context.startActivity(intent)
    }
}