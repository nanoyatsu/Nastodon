package com.nanoyatsu.nastodon.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.entity.Status

// todo @TypeConverter, @Embeded
// todo タイムライン区分
// todo (feature) アカウント区分
@Entity(tableName = "db_status")
data class DBStatus(
    @PrimaryKey(autoGenerate = true) val index: Int = 0,
    val status: String
) {
    fun asDomainModel() =
        MastodonApiManager.moshi.adapter<Status>(Status::class.java).fromJson(status)
}