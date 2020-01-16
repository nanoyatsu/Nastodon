package com.nanoyatsu.nastodon.view.timeline

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.components.networkState.NetworkState
import com.nanoyatsu.nastodon.components.networkState.NetworkStateItemViewHolder
import com.nanoyatsu.nastodon.components.networkState.NetworkStatus
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.entity.Account
import com.nanoyatsu.nastodon.data.api.entity.Attachment
import com.nanoyatsu.nastodon.data.api.entity.Status
import com.nanoyatsu.nastodon.data.database.NastodonDataBase
import com.nanoyatsu.nastodon.data.database.dao.AuthInfoDao
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.databinding.ItemTootBinding
import com.nanoyatsu.nastodon.view.accountDetail.AccountPageActivity
import com.nanoyatsu.nastodon.view.tootDetail.MediaAttachmentAdapter
import com.nanoyatsu.nastodon.view.tootDetail.TootViewModel
import kotlinx.android.synthetic.main.activity_nav_host.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class TimelineAdapter(private val context: Context) :
    PagedListAdapter<Status, RecyclerView.ViewHolder>(DiffCallback()) {
    private var authInfoDao: AuthInfoDao = NastodonDataBase.getInstance().authInfoDao()
    private lateinit var auth: AuthInfo
    private var apiManager: MastodonApiManager

    private var networkState: NetworkState? = NetworkState.LOADED

    init {
        runBlocking(context = Dispatchers.IO) { auth = authInfoDao.getAll().first() }
        apiManager = MastodonApiManager(auth.instanceUrl)
    }

    override fun getItemViewType(position: Int): Int {
        return if (!hasExtraRow(networkState) || position < super.getItemCount())
            R.layout.item_toot
        else
            R.layout.item_network_state
    }

    // overrideしたgetItemCountの数だけ描画されるため必要(getItemViewTypeの引数position等はこれを参照する)
    // super.getItemCount()は常にPagedList<Status>の要素数を返す
    override fun getItemCount(): Int =
        super.getItemCount() + if (hasExtraRow(networkState)) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_toot -> TimelineAdapter.ViewHolder.from(parent)
            R.layout.item_network_state -> NetworkStateItemViewHolder.from(parent, {})
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_toot -> {
                val toot = getItem(position)
                (holder as TimelineAdapter.ViewHolder).bind(context, toot!!, apiManager, auth)
            }
            R.layout.item_network_state -> {
                (holder as NetworkStateItemViewHolder).bind(networkState)
            }
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder is TimelineAdapter.ViewHolder) holder.binding.vm!!.vmJob.start()
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is TimelineAdapter.ViewHolder) holder.binding.vm!!.vmJob.cancel()
    }

    private fun transAccountPage(v: View, account: Account) {
        val intent = Intent(context, AccountPageActivity::class.java)
            .also { it.putExtra(AccountPageActivity.IntentKey.ACCOUNT.name, account) }
        v.context.startActivity(intent)
    }

    // github android/architecture-components-samples/PagingWithNetworkSample より
    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        this.networkState = newNetworkState
        val hadExtraRow = hasExtraRow(previousState)
        val hasExtraRow = hasExtraRow(newNetworkState)
        if (hadExtraRow != hasExtraRow) { // 前回と今回が違う
            if (hadExtraRow) notifyItemRemoved(super.getItemCount()) // 前回ExtraRowがある -> 削除
            else notifyItemInserted(super.getItemCount()) // 前回ExtraRowがない -> 追加
        } else if (hasExtraRow && previousState != newNetworkState) { // 今回ExtraRowがあって、前回と異なる
            notifyItemChanged(super.getItemCount())
        }
    }

    private fun hasExtraRow(state: NetworkState?) =
        state != null && state.status == NetworkStatus.FAILED

    class ViewHolder(val binding: ItemTootBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding =
                    ItemTootBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(context: Context, toot: Status, apiManager: MastodonApiManager, auth: AuthInfo) {
            require(context is FragmentActivity)
            setupAttachments(context, binding.attachments, toot.mediaAttachments)

            val vm = TootViewModel(toot, auth, apiManager)
            vm.replyEvent.observe(context, Observer { if (it) transTootEditAsReply(context, vm) })
            vm.reblogEvent.observe(context, Observer { if (it) vm.doReblog() })
            vm.favouriteEvent.observe(context, Observer { if (it) vm.doFav() })
            vm.timeClickEvent.observe(context, Observer { if (it) transTootDetail(context, vm) })
            binding.lifecycleOwner = context

            binding.vm = vm

            binding.executePendingBindings()
        }

        private fun setupAttachments(
            context: Context, view: RecyclerView, contents: List<Attachment>
        ) {
            val layoutManager = GridLayoutManager(context, 2)
            view.layoutManager = layoutManager
            view.adapter = MediaAttachmentAdapter(contents.toTypedArray()).apply {
                publicListener = object : MediaAttachmentAdapter.ThumbnailClickListener {
                    override val onThumbnailClick = { transImagePager(context, contents) }
                }
            }
        }

        fun transTootEditAsReply(context: Context, vm: TootViewModel) {
            if (context is FragmentActivity)
                context.main_fragment_container.findNavController().navigate(
                    TimelineFrameFragmentDirections.actionTimelineFrameFragmentToTootEditFragment(vm.toot.value!!)
                )
            vm.onReplyClickFinished()
        }

        fun transTootDetail(context: Context, vm: TootViewModel) {
//            if (context is FragmentActivity)
//                context.main_fragment_container.findNavController().navigate(
//                    MainBottomNavigationFragmentDirections.actionMainBottomNavigationFragmentToTootDetailFragment(
//                        vm.toot.value!!
//                    )
//                )
            vm.onTimeClickFinished()
        }

        fun transImagePager(context: Context, contents: List<Attachment>) {
            if (context is FragmentActivity)
                context.main_fragment_container.findNavController().navigate(
                    TimelineFrameFragmentDirections.actionTimelineFrameFragmentToImagePagerFragment(
                        contents.map { it.url }.toTypedArray()
                    )
                )
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