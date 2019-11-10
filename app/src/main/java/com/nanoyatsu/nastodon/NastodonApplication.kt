package com.nanoyatsu.nastodon

import android.app.Application
import android.content.Context
import com.nanoyatsu.nastodon.di.AppComponent
import com.nanoyatsu.nastodon.di.DaggerAppComponent

class NastodonApplication : Application() {
    companion object {
        lateinit var appContext: Context
            private set
    }

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}