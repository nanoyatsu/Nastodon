package com.nanoyatsu.nastodon.view.accountDetail

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.data.domain.Account
import com.nanoyatsu.nastodon.data.repository.account.AccountRepository
import com.nanoyatsu.nastodon.databinding.ItemAccountBinding

class AccountItemViewHolder(val binding: ItemAccountBinding, navigation: Navigation?) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup, navigation: Navigation?): AccountItemViewHolder {
            val binding =
                ItemAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return AccountItemViewHolder(binding, navigation)
        }
    }

    fun bind(context: Context, account: Account, repo: AccountRepository) {
        require(context is LifecycleOwner) { "context is not LifecycleOwner" }
        val vm = AccountViewModel(account, repo)

        binding.vm = vm
        binding.lifecycleOwner = context

        binding.executePendingBindings()
    }

    interface Navigation {
        fun transAccountDetail(account: Account)
    }
}