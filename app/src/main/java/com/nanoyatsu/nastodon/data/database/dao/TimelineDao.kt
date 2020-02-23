package com.nanoyatsu.nastodon.data.database.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nanoyatsu.nastodon.data.database.entity.DBStatus

@Dao
interface TimelineDao {
    @Query("select * from db_status WHERE timeline_kind = :timelineKind")
    fun getTimeline(timelineKind: Int): DataSource.Factory<Int, DBStatus>

    @Query("UPDATE db_status SET status = :status WHERE id = :id")
    fun update(status: String, id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(statuses: List<DBStatus>)

    @Query("DELETE FROM db_status")
    fun deleteAll()

    @Query("DELETE FROM db_status WHERE timeline_kind = :timelineKind")
    fun deleteByTimelineKind(timelineKind: Int)

    @Query("DELETE FROM db_status WHERE id = :id")
    fun deleteById(id: String)
}