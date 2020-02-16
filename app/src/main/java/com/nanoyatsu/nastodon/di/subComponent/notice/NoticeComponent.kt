package com.nanoyatsu.nastodon.di.subComponent.notice

import com.nanoyatsu.nastodon.view.common.ViewModelFactory
import com.nanoyatsu.nastodon.view.notice.NoticeViewModel
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent(modules = [NoticeModule::class])
interface NoticeComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance kind: NoticeViewModel.Kind): NoticeComponent
    }

    fun viewModelFactory(): ViewModelFactory
}