package com.nanoyatsu.nastodon.di.module.nullableToot

import com.nanoyatsu.nastodon.data.domain.Status
import com.nanoyatsu.nastodon.view.common.ViewModelFactory
import dagger.BindsInstance
import dagger.Subcomponent

// review : TootEdit replyToのための定義 たぶんかなり微妙な解決法 要検討
@Subcomponent(modules = [NullableTootModule::class])
interface NullableTootComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance toot: Status?): NullableTootComponent
    }

    fun viewModelFactory(): ViewModelFactory
}