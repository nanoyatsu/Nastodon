package com.nanoyatsu.nastodon.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.nanoyatsu.nastodon.di.module.nullableToot.NullableTootComponent
import com.nanoyatsu.nastodon.di.module.timeline.TimelineComponent
import com.nanoyatsu.nastodon.di.module.toot.TootComponent
import com.nanoyatsu.nastodon.view.NavHostActivity
import com.nanoyatsu.nastodon.view.accountDetail.AccountDetailFragment
import com.nanoyatsu.nastodon.view.accountList.AccountListFragment
import com.nanoyatsu.nastodon.view.notice.NoticeFragment
import com.nanoyatsu.nastodon.view.timeline.TimelineFragment
import com.nanoyatsu.nastodon.view.tootDetail.TootDetailFragment
import com.nanoyatsu.nastodon.view.tootEdit.TootEditFragment
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

    fun viewModelFactory(): ViewModelProvider.Factory

    fun inject(activity: NavHostActivity)
    fun inject(fragment: TimelineFragment)
    fun inject(fragment: NoticeFragment)
    fun inject(fragment: TootEditFragment)
    fun inject(fragment: TootDetailFragment)
    fun inject(fragment: AccountDetailFragment)
    fun inject(fragment: AccountListFragment)

}