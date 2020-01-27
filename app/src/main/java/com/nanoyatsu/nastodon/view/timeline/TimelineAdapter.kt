package com.nanoyatsu.nastodon.view.timeline

import android.content.Context
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.components.networkState.NetworkState
import com.nanoyatsu.nastodon.components.networkState.NetworkStateItemViewHolder
import com.nanoyatsu.nastodon.components.networkState.NetworkStatus
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.domain.Status
import com.nanoyatsu.nastodon.data.database.NastodonDataBase
import com.nanoyatsu.nastodon.data.database.dao.AuthInfoDao
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class TimelineAdapter(
    private val context: Context,
    private val navigation: TimelineItemViewHolder.Navigation? = null
) :
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
            R.layout.item_toot -> TimelineItemViewHolder.from(parent, navigation)
            R.layout.item_network_state -> NetworkStateItemViewHolder.from(parent, {})
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_toot -> {
                val toot = getItem(position)
                (holder as TimelineItemViewHolder).bind(context, toot!!, apiManager, auth)
            }
            R.layout.item_network_state -> {
                (holder as NetworkStateItemViewHolder).bind(networkState)
            }
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder is TimelineItemViewHolder) holder.binding.vm!!.vmJob.start()
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is TimelineItemViewHolder) holder.binding.vm!!.vmJob.cancel()
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