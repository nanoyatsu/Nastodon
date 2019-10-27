package com.nanoyatsu.nastodon.view.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.databinding.CardTootBinding
import com.nanoyatsu.nastodon.model.Account
import com.nanoyatsu.nastodon.model.AuthPreferenceManager
import com.nanoyatsu.nastodon.model.Status
import com.nanoyatsu.nastodon.presenter.MastodonApiManager
import com.nanoyatsu.nastodon.view.AccountPageActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class TimelineAdapter(private val context: Context, private val toots: ArrayList<Status>) :
    RecyclerView.Adapter<TimelineAdapter.ViewHolder>() {

    override fun getItemCount(): Int = toots.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            DataBindingUtil.inflate<CardTootBinding>(LayoutInflater.from(context), R.layout.card_toot, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(vh: ViewHolder, position: Int) {
        vh.binding.toot = toots[position]
        val toot = vh.binding.toot!! // fixme 書き方がかなり怪しい (対nullableとか実質aliasがほしいだけとか) 調べる
        Glide.with(this.context).load(toot.account.avatarStatic).circleCrop().into(vh.binding.accountAvatar)
        vh.binding.accountAvatar.setOnClickListener { transAccountPage(it, toot.account) }

        vh.binding.buttonRepeat.setOnClickListener {
            resetStatus(
                position,
                doReblog(toots[position].id, toots[position].reblogged ?: false)
            )
        }
        vh.binding.buttonStar.setOnClickListener {
            resetStatus(
                position,
                doFav(it, toots[position].id, toots[position].favourited ?: false)
            )
        }
    }

    private fun transAccountPage(v: View, account: Account) {
        val intent = Intent(context, AccountPageActivity::class.java)
            .also { it.putExtra(AccountPageActivity.IntentKey.ACCOUNT.name, account) }
        v.context.startActivity(intent)
    }

    private fun doReblog(id: String, reblogged: Boolean): Status? {
        val pref = AuthPreferenceManager(context)
        val api = MastodonApiManager(pref.instanceUrl).statuses
        if (reblogged) // todo アイコンの色変える→失敗したら戻す
            return runBlocking(Dispatchers.IO) { api.unReblog(pref.accessToken, id).body() }
        else
            return runBlocking(Dispatchers.IO) { api.reblog(pref.accessToken, id).body() }
    }

    private fun doFav(view: View, id: String, favourited: Boolean): Status? { // todo doReblogと抽象化
        val pref = AuthPreferenceManager(context)
        val api = MastodonApiManager(pref.instanceUrl).favourites
        if (favourited) {
            view.background.setTint(Color.GRAY)
            return runBlocking(Dispatchers.IO) {
                val res = api.unFavourite(pref.accessToken, id)
                res.body()
            }
        } else {
            view.background.setTint(context.getColor(R.color.colorPrimary))
            return runBlocking(Dispatchers.IO) {
                val res = api.favourite(pref.accessToken, id)
                res.body()
            }
        }
    }

    private fun resetStatus(position: Int, new: Status?) {
        if (new == null) return
        toots[position] = new
        notifyDataSetChanged()
//        notifyItemChanged(position) // todo payload // https://qiita.com/ralph/items/e56844976117d9883e34
    }

    class ViewHolder(val binding: CardTootBinding) : RecyclerView.ViewHolder(binding.root) {}
}