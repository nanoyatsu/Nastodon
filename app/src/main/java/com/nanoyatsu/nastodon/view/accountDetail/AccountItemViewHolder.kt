package com.nanoyatsu.nastodon.view.accountDetail

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.data.domain.Account
import com.nanoyatsu.nastodon.data.repository.account.AccountRepository
import com.nanoyatsu.nastodon.databinding.ItemAccountBinding

class AccountItemViewHolder(val binding: ItemAccountBinding, val navigation: Navigation?) :
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
        vm.avatarClickEvent.observe(context, Observer { if (it) onAvatarClick(vm) })
        vm.followEvent.observe(context, Observer { if (it) onFollowButton(vm) })

        binding.vm = vm
        binding.lifecycleOwner = context

        binding.executePendingBindings()
    }

    private fun onAvatarClick(vm: AccountViewModel) {
        navigation?.transAccountDetail(vm.account)
        vm.onAvatarClickFinished()
    }

    private fun onFollowButton(vm: AccountViewModel) {
        vm.switchFollow()
        vm.onFollowClickFinished()
    }

    interface Navigation {
        fun transAccountDetail(account: Account)
    }
}