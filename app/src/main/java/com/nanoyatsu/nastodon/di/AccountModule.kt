package com.nanoyatsu.nastodon.di

import com.nanoyatsu.nastodon.data.database.NastodonDataBase
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

@Module
class AccountModule {
    @Provides // todo たぶんアプリケーションレベルの範囲のModuleに移動したほうがよい
    fun provideDB(): NastodonDataBase = NastodonDataBase.getInstance()

    @Provides // todo マルチアカウント考慮
    fun tryProvideAuthInfo(db: NastodonDataBase): AuthInfo =
        runBlocking(context = Dispatchers.IO) { db.authInfoDao().getAll().firstOrNull() ?: AuthInfo() }

    @Provides
//    fun provideApiManager(authInfo: AuthInfo?): MastodonApiManager = MastodonApiManager("https://qiitadon.com")
    fun provideApiManager(authInfo: AuthInfo?): MastodonApiManager = MastodonApiManager(authInfo?.instanceUrl ?: "")

}