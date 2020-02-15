package com.nanoyatsu.nastodon.di.subComponent.account

import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.di.annotation.ViewModelKey
import com.nanoyatsu.nastodon.view.accountDetail.AccountViewModel
import com.nanoyatsu.nastodon.view.accountList.AccountListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface AccountModule {
    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    fun bindAccountViewModel(viewModel: AccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AccountListViewModel::class)
    fun bindAccountListViewModel(viewModel: AccountListViewModel): ViewModel

}