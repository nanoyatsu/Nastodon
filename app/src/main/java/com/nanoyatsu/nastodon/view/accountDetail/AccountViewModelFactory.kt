package com.nanoyatsu.nastodon.view.accountDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AccountViewModelFactory(
    val account: com.nanoyatsu.nastodon.data.domain.Account
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            return AccountViewModel(account) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}