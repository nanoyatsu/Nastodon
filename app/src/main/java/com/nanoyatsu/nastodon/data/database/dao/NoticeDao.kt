package com.nanoyatsu.nastodon.data.database.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nanoyatsu.nastodon.data.database.entity.DBNotice

@Dao
interface NoticeDao {
    @Query("select * from db_notice WHERE notice_kind = :noticeKind")
    fun getNotice(noticeKind: Int): DataSource.Factory<Int, DBNotice>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(videos: List<DBNotice>)

    @Query("DELETE FROM db_notice")
    fun deleteAll()

    @Query("DELETE FROM db_notice WHERE notice_kind = :noticeKind")
    fun deleteByNoticeKind(noticeKind: Int)
}