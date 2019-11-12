package com.nanoyatsu.nastodon.di

import android.content.Context
import com.nanoyatsu.nastodon.data.NastodonDataBase
import com.nanoyatsu.nastodon.view.BootActivity
import com.nanoyatsu.nastodon.view.MainActivity
import dagger.BindsInstance
import dagger.Component
import dagger.Provides

@Component(modules = [AccountModule::class])
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(activity: BootActivity)
    fun inject(activity: MainActivity)
}