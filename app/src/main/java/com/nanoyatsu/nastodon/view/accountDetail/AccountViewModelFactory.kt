package com.nanoyatsu.nastodon.view.accountDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nanoyatsu.nastodon.data.domain.Account
import com.nanoyatsu.nastodon.data.repository.account.AccountRepository

class AccountViewModelFactory(
    private val account: Account,
    private val repo: AccountRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            return AccountViewModel(account, repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}