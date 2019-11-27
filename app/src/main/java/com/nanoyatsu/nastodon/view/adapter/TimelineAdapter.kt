package com.nanoyatsu.nastodon.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.data.NastodonDataBase
import com.nanoyatsu.nastodon.data.dao.AuthInfoDao
import com.nanoyatsu.nastodon.data.entity.AuthInfo
import com.nanoyatsu.nastodon.databinding.CardTootBinding
import com.nanoyatsu.nastodon.model.Account
import com.nanoyatsu.nastodon.model.Status
import com.nanoyatsu.nastodon.presenter.MastodonApiManager
import com.nanoyatsu.nastodon.view.AccountPageActivity
import com.nanoyatsu.nastodon.view.AccountsAdapter
import com.nanoyatsu.nastodon.viewModel.CardTootViewModel
import com.nanoyatsu.nastodon.viewModel.CardTootViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class TimelineAdapter(private val context: Context, private val toots: List<Status>) :
    ListAdapter<Status, TimelineAdapter.ViewHolder>(DiffCallback()) {
    private var authInfoDao: AuthInfoDao = NastodonDataBase.getInstance().authInfoDao()
    private lateinit var auth: AuthInfo
    private lateinit var apiManager: MastodonApiManager

    init {
        runBlocking(context = Dispatchers.IO) { auth = authInfoDao.getAll().first() }
        apiManager = MastodonApiManager(auth.instanceUrl)
    }

    override fun getItemCount(): Int = toots.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            DataBindingUtil.inflate<CardTootBinding>(
                LayoutInflater.from(context),
                R.layout.card_toot,
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val toot = getItem(position)
        holder.bind(toot)
    }

    private fun transAccountPage(v: View, account: Account) {
        val intent = Intent(context, AccountPageActivity::class.java)
            .also { it.putExtra(AccountPageActivity.IntentKey.ACCOUNT.name, account) }
        v.context.startActivity(intent)
    }

    private fun doReblog(id: String, reblogged: Boolean): Status? {
        val api = apiManager.statuses
        if (reblogged) // todo アイコンの色変える→失敗したら戻す
            return runBlocking(Dispatchers.IO) { api.unReblog(auth.accessToken, id).body() }
        else
            return runBlocking(Dispatchers.IO) { api.reblog(auth.accessToken, id).body() }
    }

    private fun doFav(view: View, id: String, favourited: Boolean): Status? { // todo doReblogと抽象化
        val api = apiManager.favourites
        if (favourited) {
//            view.background.setTint(Color.GRAY)
            return runBlocking(Dispatchers.IO) {
                val res = api.unFavourite(auth.accessToken, id)
                res.body()
            }
        } else {
//            view.background.setTint(context.getColor(R.color.colorPrimary))
            return runBlocking(Dispatchers.IO) {
                val res = api.favourite(auth.accessToken, id)
                res.body()
            }
        }
    }

    private fun resetStatus(position: Int, new: Status?) {
        if (new == null) return
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: CardTootBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(toot: Status) {
            binding.vm = CardTootViewModelFactory(toot).create(CardTootViewModel::class.java)
            Glide.with(binding.root.context).load(toot.account.avatarStatic).circleCrop()
                .into(binding.accountAvatar)
//            binding.accountAvatar.setOnClickListener { transAccountPage(it, toot.account) }

//            binding.buttonRepeat.setOnClickListener {
//                resetStatus(
//                    position,
//                    doReblog(toot.id, toot.reblogged ?: false)
//                )
//            }
//            binding.buttonStar.setOnClickListener {
//                resetStatus(
//                    position,
//                    doFav(it, toot.id, toot.favourited ?: false)
//                )
//            }
            binding.executePendingBindings()
        }
    }

    companion object {
        class DiffCallback : DiffUtil.ItemCallback<Status>() {
            override fun areItemsTheSame(oldItem: Status, newItem: Status): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Status, newItem: Status): Boolean {
                return oldItem == newItem
            }

        }
    }
}