package com.nanoyatsu.nastodon.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.nanoyatsu.nastodon.di.subComponent.account.AccountComponent
import com.nanoyatsu.nastodon.di.subComponent.notice.NoticeComponent
import com.nanoyatsu.nastodon.di.subComponent.nullableToot.NullableTootComponent
import com.nanoyatsu.nastodon.di.subComponent.timeline.TimelineComponent
import com.nanoyatsu.nastodon.di.subComponent.toot.TootComponent
import com.nanoyatsu.nastodon.view.NavHostActivity
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        AppModule::class,
        BindModule::class
    ]
)
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun tootComponent(): TootComponent.Factory
    fun nullableTootComponent(): NullableTootComponent.Factory
    fun timelineComponent(): TimelineComponent.Factory
    fun noticeComponent(): NoticeComponent.Factory
    fun accountComponent(): AccountComponent.Factory

    fun viewModelFactory(): ViewModelProvider.Factory

    fun inject(activity: NavHostActivity)
}