package com.nanoyatsu.nastodon.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nanoyatsu.nastodon.data.database.entity.DBStatus

@Dao
interface TimelineDao {
    @Query("select * from db_status")
    fun getTimeline(): LiveData<List<DBStatus>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(videos: List<DBStatus>)

    @Query("DELETE FROM db_status")
    fun deleteAll()
}