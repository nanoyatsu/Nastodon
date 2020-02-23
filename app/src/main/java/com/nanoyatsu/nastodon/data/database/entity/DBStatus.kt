package com.nanoyatsu.nastodon.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.domain.Status

// todo @TypeConverter, @Embeded
// todo (feature) アカウント区分
@Entity(tableName = "db_status", primaryKeys = ["id", "timeline_kind"])
data class DBStatus(
    val id: String,
    @ColumnInfo(name = "timeline_kind") val timelineKind: Int,
    val status: String
) {
    fun asDomainModel() =
        requireNotNull(MastodonApiManager.moshi.adapter<Status>(Status::class.java).fromJson(status))
}