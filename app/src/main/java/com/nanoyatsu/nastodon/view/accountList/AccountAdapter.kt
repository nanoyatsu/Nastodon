package com.nanoyatsu.nastodon.view.accountList

import android.content.Context
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.nanoyatsu.nastodon.components.networkState.NetworkState
import com.nanoyatsu.nastodon.components.networkState.NetworkStatus
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiAccounts
import com.nanoyatsu.nastodon.data.domain.Account
import com.nanoyatsu.nastodon.data.repository.account.AccountRepository
import com.nanoyatsu.nastodon.view.accountDetail.AccountItemViewHolder

class AccountAdapter(
    private val context: Context,
    private val apiDir: MastodonApiAccounts,
    private val token: String,
    private val navigation: AccountItemViewHolder.Navigation? = null
) :
    PagedListAdapter<Account, AccountItemViewHolder>(DiffCallback()) {

    private var networkState: NetworkState? = NetworkState.LOADED

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountItemViewHolder {
        return AccountItemViewHolder.from(parent, navigation)
    }

    override fun onBindViewHolder(holder: AccountItemViewHolder, position: Int) {
        val account = requireNotNull(getItem(position))
        // todo AccountIdをメソッド引数にしてインスタンス都度生成不要にする
        val repo = AccountRepository(apiDir, token, account.id)
        holder.bind(context, account, repo)
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