package com.nanoyatsu.nastodon.di

import com.nanoyatsu.nastodon.data.NastodonDataBase
import com.nanoyatsu.nastodon.data.entity.AuthInfo
import com.nanoyatsu.nastodon.presenter.MastodonApiManager
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

@Module
class AccountModule {
    @Provides // todo たぶんアプリケーションレベルの範囲のModuleに移動したほうがよい
    fun provideDB(): NastodonDataBase = NastodonDataBase.getInstance()

    @Provides
    fun tryProvideAuthInfo(db: NastodonDataBase): AuthInfo? =
        runBlocking(context = Dispatchers.IO) { db.authInfoDao().getAll().firstOrNull() }

    @Provides
    fun provideApiManager(authInfo: AuthInfo?): MastodonApiManager = MastodonApiManager(authInfo?.instanceUrl ?: "")

}