package com.nanoyatsu.nastodon.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nanoyatsu.nastodon.data.entity.AuthInfo

@Dao
interface AuthInfoDao {
    @Query("SELECT * FROM auth_info")
    fun getAll(): List<AuthInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(row: AuthInfo)
}