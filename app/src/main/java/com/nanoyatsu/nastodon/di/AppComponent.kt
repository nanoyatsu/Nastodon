package com.nanoyatsu.nastodon.di

import android.content.Context
import com.nanoyatsu.nastodon.view.MainActivity
import dagger.BindsInstance
import dagger.Component

@Component(modules = [AccountModule::class])
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(activity: MainActivity)
}