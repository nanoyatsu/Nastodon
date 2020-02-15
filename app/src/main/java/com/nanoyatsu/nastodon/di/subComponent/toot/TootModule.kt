package com.nanoyatsu.nastodon.di.subComponent.toot

import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.di.annotation.ViewModelKey
import com.nanoyatsu.nastodon.view.tootDetail.TootViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface TootModule {
    @Binds
    @IntoMap
    @ViewModelKey(TootViewModel::class)
    fun bindTootViewModel(viewModel: TootViewModel): ViewModel
}