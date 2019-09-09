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

class TimelineAdapter(
    context: Context,
    private val toots: Array<Status>
) :
    ArrayAdapter<Status>(context, R.layout.card_toot, toots) {

    // fixme スクロールのたびに通信をする影響？でかなり怒られる 挙動も重たくなっている ↓調べて直す
    //  E/SELinux: avc:  denied  { find } for interface=vendor.qti.hardware.perf::IPerf sid=u:r:untrusted_app:s0:c89,c257,c512,c768 pid=29990 scontext=u:r:untrusted_app:s0:c89,c257,c512,c768 tcontext=u:object_r:hal_perf_hwservice:s0 tclass=hwservice_manager permissive=0
    //  29990-29990/com.nanoyatsu.nastodon E/ANDR-PERF: IPerf::tryGetService failed!
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val inflater: LayoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val thisView = convertView ?: inflater.inflate(R.layout.card_toot, parent, false)

        thisView.findViewById<TextView>(R.id.displayName)?.text = toots[position].account.displayName
        thisView.findViewById<TextView>(R.id.username)?.text = toots[position].account.username
        thisView.findViewById<TextView>(R.id.note)?.text =
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