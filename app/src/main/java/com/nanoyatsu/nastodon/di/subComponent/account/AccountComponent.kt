package com.nanoyatsu.nastodon.di.subComponent.account

import com.nanoyatsu.nastodon.data.domain.Account
import com.nanoyatsu.nastodon.view.common.ViewModelFactory
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent(modules = [AccountModule::class])
interface AccountComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance account: Account): AccountComponent
    }

    fun viewModelFactory(): ViewModelFactory
}