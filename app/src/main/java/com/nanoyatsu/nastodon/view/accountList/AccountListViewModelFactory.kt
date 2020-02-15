package com.nanoyatsu.nastodon.view.accountList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nanoyatsu.nastodon.data.domain.Account
import com.nanoyatsu.nastodon.data.repository.account.AccountRepository

class AccountListViewModelFactory(
    private val kind: AccountListViewModel.Kind,
    private val account: Account,
    private val repo: AccountRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountListViewModel::class.java)) {
            return AccountListViewModel(kind, account, repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}