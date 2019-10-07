package com.nanoyatsu.nastodon.view.adapter

import android.content.Context
import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.model.Account
import com.nanoyatsu.nastodon.model.AuthPreferenceManager
import com.nanoyatsu.nastodon.model.Status
import com.nanoyatsu.nastodon.presenter.MastodonApiManager
import com.nanoyatsu.nastodon.view.AccountPageActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class TimelineAdapter(private val context: Context, private val toots: ArrayList<Status>) :
    RecyclerView.Adapter<TimelineAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return toots.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val toot = LayoutInflater.from(context).inflate(R.layout.card_toot, parent, false) as ConstraintLayout
        return ViewHolder(toot)
    }

    // fixme スクロールのたびに通信をする影響？でかなり怒られる 挙動も重たくなっている ↓調べて直す
    //  E/SELinux: avc:  denied  { find } for interface=vendor.qti.hardware.perf::IPerf sid=u:r:untrusted_app:s0:c89,c257,c512,c768 pid=29990 scontext=u:r:untrusted_app:s0:c89,c257,c512,c768 tcontext=u:object_r:hal_perf_hwservice:s0 tclass=hwservice_manager permissive=0
    //  29990-29990/com.nanoyatsu.nastodon E/ANDR-PERF: IPerf::tryGetService failed!
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val toot = toots[position]
        Glide.with(this.context).load(toot.account.avatarStatic).circleCrop().into(holder.accountAvatar)
        holder.accountAvatar.setOnClickListener { transAccountPage(it, toot.account) }

        holder.displayName.text = toot.account.displayName
        holder.username.text = toot.account.username
        holder.note.text = Html.fromHtml(toot.content, Html.FROM_HTML_MODE_COMPACT)

        holder.buttonReblog.setOnClickListener { resetStatus(position, doReblog(toot.id, toot.reblogged ?: false)) }
        holder.buttonFav.setOnClickListener { resetStatus(position, doFav(toot.id, toot.favourited ?: false)) }
    }

    private fun transAccountPage(v: View, account: Account) {
        val intent = Intent(context, AccountPageActivity::class.java)
            .also { it.putExtra(AccountPageActivity.IntentKey.ACCOUNT.name, account) }
        v.context.startActivity(intent)
    }

    // fixme たぶんまだ動いてない
    private fun doReblog(id: String, reblogged: Boolean): Status? {
        val pref = AuthPreferenceManager(context)
        val api = MastodonApiManager(pref.instanceUrl).statuses
        if (reblogged) // todo アイコンの色変える→失敗したら戻す
            return runBlocking(Dispatchers.IO) { api.unReblog(id).body() }
        else
            return runBlocking(Dispatchers.IO) { api.reblog(id).body() }
    }

    private fun doFav(id: String, favourited: Boolean): Status? { // todo doReblogと抽象化
        val pref = AuthPreferenceManager(context)
        val api = MastodonApiManager(pref.instanceUrl).statuses
        if (favourited)
            return runBlocking(Dispatchers.IO) {
                val res = api.unPin(id)
                res.body()
            }
        else
            return runBlocking(Dispatchers.IO) {
                val res = api.pin(id)
                res.body()
            }

    }

    private fun resetStatus(position: Int, new: Status?) {
        if (new == null) return
        toots[position] = new
        notifyItemChanged(position) // todo payload // https://qiita.com/ralph/items/e56844976117d9883e34
    }

    class ViewHolder(toot: ConstraintLayout) : RecyclerView.ViewHolder(toot) {
        val displayName: TextView = toot.findViewById(R.id.displayName)
        val username: TextView = toot.findViewById(R.id.username)
        val note: TextView = toot.findViewById(R.id.note)
        val accountAvatar: ImageView = toot.findViewById(R.id.accountAvatar)
        val buttonReply: ImageButton = toot.findViewById(R.id.button_reply)
        val buttonReblog: ImageButton = toot.findViewById(R.id.button_repeat)
        val buttonFav: ImageButton = toot.findViewById(R.id.button_star)
    }
}