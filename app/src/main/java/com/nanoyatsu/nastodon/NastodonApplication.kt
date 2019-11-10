package com.nanoyatsu.nastodon

import android.app.Application
import android.content.Context

class NastodonApplication : Application() {
    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}