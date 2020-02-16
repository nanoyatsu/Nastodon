package com.nanoyatsu.nastodon.di.subComponent.account.list

import com.nanoyatsu.nastodon.view.accountList.AccountListViewModel
import com.nanoyatsu.nastodon.view.common.ViewModelFactory
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent(modules = [AccountListModule::class])
interface AccountListComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance kind: AccountListViewModel.Kind): AccountListComponent
    }

    fun viewModelFactory(): ViewModelFactory
}