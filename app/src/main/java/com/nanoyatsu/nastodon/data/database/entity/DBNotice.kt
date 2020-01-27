package com.nanoyatsu.nastodon.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.domain.Notification

@Entity(tableName = "db_notice")
data class DBNotice(
    @PrimaryKey(autoGenerate = true) val index: Int = 0,
    @ColumnInfo(name = "notice_kind") val noticeKind: Int,
    val notice: String
) {
    fun asDomainModel() =
        requireNotNull(
            MastodonApiManager.moshi.adapter<Notification>(Notification::class.java)
                .fromJson(notice)
        )
}