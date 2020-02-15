package com.nanoyatsu.nastodon.di.subComponent.timeline

import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.di.annotation.ViewModelKey
import com.nanoyatsu.nastodon.view.timeline.TimelineViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface TimelineModule {
    @Binds
    @IntoMap
    @ViewModelKey(TimelineViewModel::class)
    fun bindTimelineViewModel(viewModel: TimelineViewModel): ViewModel
}