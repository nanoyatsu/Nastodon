package com.nanoyatsu.nastodon.view.notice

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.components.networkState.NetworkState
import com.nanoyatsu.nastodon.components.networkState.NetworkStateItemViewHolder
import com.nanoyatsu.nastodon.components.networkState.NetworkStatus
import com.nanoyatsu.nastodon.data.api.entity.Notification
import com.nanoyatsu.nastodon.data.api.entity.NotificationType
import com.nanoyatsu.nastodon.databinding.ItemNoticeBinding
import com.nanoyatsu.nastodon.resource.NoticeIcon

class NoticeAdapter(private val context: Context) :
    PagedListAdapter<Notification, RecyclerView.ViewHolder>(DiffCallback()) {

    private var networkState: NetworkState? = NetworkState.LOADED

    override fun getItemViewType(position: Int): Int {
        return if (!hasExtraRow(networkState) || position < super.getItemCount())
            R.layout.item_notice
        else
            R.layout.item_network_state
    }

    // overrideしたgetItemCountの数だけ描画されるため必要(getItemViewTypeの引数position等はこれを参照する)
    // super.getItemCount()は常にPagedList<Status>の要素数を返す
    override fun getItemCount(): Int =
        super.getItemCount() + if (hasExtraRow(networkState)) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_notice -> NoticeAdapter.ViewHolder.from(parent)
            R.layout.item_network_state -> NetworkStateItemViewHolder.from(parent, {})
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_notice -> (holder as NoticeAdapter.ViewHolder).bind(
                context,
                getItem(position)!!
            )
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

    class ViewHolder(val binding: ItemNoticeBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding =
                    ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(context: Context, notice: Notification) {
            binding.notice = notice

            val type = NotificationType.values().firstOrNull { it.value == notice.type }

            // todo : Replyは別レイアウトにする
            val descriptionId: Int
            val icon: NoticeIcon
            if (type == null) {
                descriptionId = R.string.noticeDescriptionUndefined
                icon = NoticeIcon.UNDEFINED
            } else {
                descriptionId = type.descriptionId
                icon = type.icon
            }

            binding.description.text = context.getString(descriptionId, notice.account.displayName)
            binding.typeIcon.background =
                context.getDrawable(icon.iconId)?.apply { setTint(context.getColor(icon.colorId)) }
        }
    }

    companion object {
        class DiffCallback : DiffUtil.ItemCallback<Notification>() {
            override fun areItemsTheSame(oldItem: Notification, newItem: Notification) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Notification, newItem: Notification) =
                oldItem == newItem
        }
    }
}