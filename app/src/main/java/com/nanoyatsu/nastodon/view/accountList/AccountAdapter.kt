package com.nanoyatsu.nastodon.view.accountList

import android.content.Context
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.components.networkState.NetworkState
import com.nanoyatsu.nastodon.components.networkState.NetworkStateItemViewHolder
import com.nanoyatsu.nastodon.components.networkState.NetworkStatus
import com.nanoyatsu.nastodon.data.domain.Account
import com.nanoyatsu.nastodon.view.accountDetail.AccountItemViewHolder

class AccountAdapter(
    private val context: Context,
    private val navigation: AccountItemViewHolder.Navigation? = null
) :
    PagedListAdapter<Account, RecyclerView.ViewHolder>(DiffCallback()) {

    private var networkState: NetworkState? = NetworkState.LOADED

    override fun getItemCount(): Int =
        super.getItemCount() + if (hasExtraRow(networkState)) 1 else 0

    override fun getItemViewType(position: Int): Int {
        return if (!hasExtraRow(networkState) || position < super.getItemCount())
            R.layout.item_account
        else
            R.layout.item_network_state
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_account -> AccountItemViewHolder.from(parent, navigation)
            R.layout.item_network_state -> NetworkStateItemViewHolder.from(parent, {})
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AccountItemViewHolder -> {
                val account = requireNotNull(getItem(position))
                // todo AccountIdをメソッド引数にしてインスタンス都度生成不要にする
                holder.bind(context, account)
            }
            is NetworkStateItemViewHolder -> {
                holder.bind(networkState)
            }
        }
    }

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
        class DiffCallback : DiffUtil.ItemCallback<Account>() {
            override fun areItemsTheSame(oldItem: Account, newItem: Account): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Account, newItem: Account): Boolean =
                oldItem == newItem
        }
    }
}