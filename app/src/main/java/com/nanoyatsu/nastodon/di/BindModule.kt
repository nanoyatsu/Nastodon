package com.nanoyatsu.nastodon.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nanoyatsu.nastodon.di.annotation.ViewModelKey
import com.nanoyatsu.nastodon.view.common.ViewModelFactory
import com.nanoyatsu.nastodon.view.timeline.TimelineFrameViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface BindModule {
    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(TimelineFrameViewModel::class)
    fun bindTimelineFrameViewModel(viewModel: TimelineFrameViewModel): ViewModel
}