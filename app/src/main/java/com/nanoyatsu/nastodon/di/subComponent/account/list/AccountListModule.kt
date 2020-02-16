package com.nanoyatsu.nastodon.di.subComponent.account.list

import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.di.annotation.ViewModelKey
import com.nanoyatsu.nastodon.view.accountList.AccountListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface AccountListModule {
    @Binds
    @IntoMap
    @ViewModelKey(AccountListViewModel::class)
    fun bindAccountListViewModel(viewModel: AccountListViewModel): ViewModel
}