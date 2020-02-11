package com.nanoyatsu.nastodon.di.module.toot

import com.nanoyatsu.nastodon.data.domain.Status
import com.nanoyatsu.nastodon.view.common.ViewModelFactory
import dagger.BindsInstance
import dagger.Subcomponent


@Subcomponent(modules = [TootModule::class])
interface TootComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance toot: Status): TootComponent
    }

    fun viewModelFactory(): ViewModelFactory
}