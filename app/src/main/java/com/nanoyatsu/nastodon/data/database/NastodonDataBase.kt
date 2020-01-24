package com.nanoyatsu.nastodon.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.data.database.dao.AuthInfoDao
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo

@Database(entities = [AuthInfo::class], version = 1, exportSchema = true)
abstract class NastodonDataBase : RoomDatabase() {
    abstract fun authInfoDao(): AuthInfoDao

    companion object {
        private var INSTANCE: NastodonDataBase? = null

        @Synchronized
        fun getInstance(): NastodonDataBase {
            if (INSTANCE is NastodonDataBase) return INSTANCE!!
            INSTANCE =
                Room.databaseBuilder(
                    NastodonApplication.appContext,
                    NastodonDataBase::class.java,
                    "nastodon_database"
                ).build()
            return INSTANCE!!
        }
    }
}