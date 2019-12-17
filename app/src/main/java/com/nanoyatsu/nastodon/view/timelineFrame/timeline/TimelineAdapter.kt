package com.nanoyatsu.nastodon.view.timelineFrame.timeline

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.entity.Account
import com.nanoyatsu.nastodon.data.api.entity.Status
import com.nanoyatsu.nastodon.data.database.NastodonDataBase
import com.nanoyatsu.nastodon.data.database.dao.AuthInfoDao
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.databinding.ItemTootBinding
import com.nanoyatsu.nastodon.view.accountDetail.AccountPageActivity
import com.nanoyatsu.nastodon.view.timelineFrame.TimelineFrameFragmentDirections
import com.nanoyatsu.nastodon.view.tootDetail.TootViewModel
import kotlinx.android.synthetic.main.activity_nav_host.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class TimelineAdapter(private val context: Context) :
    PagedListAdapter<Status, TimelineAdapter.ViewHolder>(
        DiffCallback()
    ) {
    private var authInfoDao: AuthInfoDao = NastodonDataBase.getInstance().authInfoDao()
    private lateinit var auth: AuthInfo
    private var apiManager: MastodonApiManager

    init {
        runBlocking(context = Dispatchers.IO) { auth = authInfoDao.getAll().first() }
        apiManager = MastodonApiManager(auth.instanceUrl)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(
            parent
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val toot = getItem(position)
        holder.bind(context, toot!!, apiManager, auth)
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.binding.vm!!.vmJob.start()
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.binding.vm!!.vmJob.cancel()
    }

    private fun transAccountPage(v: View, account: Account) {
        val intent = Intent(context, AccountPageActivity::class.java)
            .also { it.putExtra(AccountPageActivity.IntentKey.ACCOUNT.name, account) }
        v.context.startActivity(intent)
    }

    class ViewHolder(val binding: ItemTootBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = DataBindingUtil.inflate<ItemTootBinding>(
                    LayoutInflater.from(parent.context), R.layout.item_toot, parent, false
                )
                return ViewHolder(
                    binding
                )
            }
        }

        fun bind(context: Context, toot: Status, apiManager: MastodonApiManager, auth: AuthInfo) {
            require(context is FragmentActivity)
            val vm = TootViewModel(
                toot,
                auth,
                apiManager
            )
            vm.reblogEvent.observe(context, Observer { if (it) vm.doReblog() })
            vm.favouriteEvent.observe(context, Observer { if (it) vm.doFav() })
            vm.timeClickEvent.observe(context, Observer { if (it) transTootDetail(context, vm) })
            binding.lifecycleOwner = context

            binding.vm = vm

            binding.executePendingBindings()
        }

        fun transTootDetail(context: Context, vm: TootViewModel) {
            if (context is FragmentActivity)
                context.main_fragment_container.findNavController().navigate(
                    TimelineFrameFragmentDirections.actionTimelineFrameFragmentToTootDetailFragment(
                        vm.toot.value!!
                    )
                )

            vm.onTimeClickFinished()
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