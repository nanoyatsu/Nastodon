package com.nanoyatsu.nastodon.view.notice

import android.content.Context
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.components.networkState.NetworkState
import com.nanoyatsu.nastodon.components.networkState.NetworkStateItemViewHolder
import com.nanoyatsu.nastodon.components.networkState.NetworkStatus
import com.nanoyatsu.nastodon.data.domain.Notification
import com.nanoyatsu.nastodon.data.domain.NotificationType
import com.nanoyatsu.nastodon.view.timeline.TimelineItemViewHolder

class NoticeAdapter(
    private val context: Context,
    private val noticeNavigation: NoticeItemViewHolder.Navigation? = null,
    private val tootNavigation: TimelineItemViewHolder.Navigation? = null
) :
    PagedListAdapter<Notification, RecyclerView.ViewHolder>(DiffCallback()) {

    private var networkState: NetworkState? = NetworkState.LOADED

    override fun getItemViewType(position: Int): Int {
        if (hasExtraRow(networkState) && position >= super.getItemCount())
            return R.layout.item_network_state

        val item = requireNotNull(getItem(position))
        if (item.type == NotificationType.MENTION)
            return R.layout.item_toot
        return R.layout.item_notice
    }

    // overrideしたgetItemCountの数だけ描画されるため必要(getItemViewTypeの引数position等はこれを参照する)
    // super.getItemCount()は常にPagedList<Status>の要素数を返す
    override fun getItemCount(): Int =
        super.getItemCount() + if (hasExtraRow(networkState)) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_notice -> NoticeItemViewHolder.from(parent, noticeNavigation)
            R.layout.item_toot -> TimelineItemViewHolder.from(parent, tootNavigation)
            R.layout.item_network_state -> NetworkStateItemViewHolder.from(parent, {})
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_toot -> {
                val toot = requireNotNull(getItem(position)?.status)
                (holder as TimelineItemViewHolder).bind(context, toot)
            }
            R.layout.item_notice -> {
                val notice = requireNotNull(getItem(position))
                (holder as NoticeItemViewHolder).bind(context, notice)
            }
            R.layout.item_network_state -> (holder as NetworkStateItemViewHolder).bind(networkState)
        }
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
        class DiffCallback : DiffUtil.ItemCallback<Notification>() {
            override fun areItemsTheSame(oldItem: Notification, newItem: Notification) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Notification, newItem: Notification) =
                oldItem == newItem
        }
    }
}