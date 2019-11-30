package com.nanoyatsu.nastodon.di

import android.content.Context
import com.nanoyatsu.nastodon.view.splash.BootActivity
import com.nanoyatsu.nastodon.view.timeline.MainActivity
import com.nanoyatsu.nastodon.view.tootEdit.TootEditActivity
import dagger.BindsInstance
import dagger.Component

@Component(modules = [AccountModule::class])
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(activity: BootActivity)
    fun inject(activity: MainActivity)
    fun inject(activity: TootEditActivity)
}