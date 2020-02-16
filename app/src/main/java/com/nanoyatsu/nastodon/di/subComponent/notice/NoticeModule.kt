package com.nanoyatsu.nastodon.di.subComponent.notice

import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.di.annotation.ViewModelKey
import com.nanoyatsu.nastodon.view.notice.NoticeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface NoticeModule {
    @Binds
    @IntoMap
    @ViewModelKey(NoticeViewModel::class)
    fun bindNoticeViewModel(viewModel: NoticeViewModel): ViewModel
}