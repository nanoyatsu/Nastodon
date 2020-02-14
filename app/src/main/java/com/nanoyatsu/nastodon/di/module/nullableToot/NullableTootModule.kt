package com.nanoyatsu.nastodon.di.module.nullableToot

import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.di.annotation.ViewModelKey
import com.nanoyatsu.nastodon.view.tootEdit.TootEditViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface NullableTootModule {
    @Binds
    @IntoMap
    @ViewModelKey(TootEditViewModel::class)
    fun bindTootEditViewModel(viewModel: TootEditViewModel): ViewModel
}