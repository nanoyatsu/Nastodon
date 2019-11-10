package com.nanoyatsu.nastodon.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.data.dao.AuthInfoDao
import com.nanoyatsu.nastodon.data.entity.AuthInfo

@Database(entities = [AuthInfo::class], version = 1, exportSchema = false)
abstract class NastodonDataBase : RoomDatabase() {
    abstract fun authInfoDao(): AuthInfoDao

    companion object {
        private var INSTANCE: NastodonDataBase? = null

        @Synchronized
        fun getInstance(): NastodonDataBase {
            if (INSTANCE is NastodonDataBase) return INSTANCE!!
            INSTANCE =
                Room.databaseBuilder(NastodonApplication.appContext, NastodonDataBase::class.java, "TODO_DB").build()
            return INSTANCE!!
        }
    }

}