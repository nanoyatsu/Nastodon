package com.nanoyatsu.nastodon.data.api.entity


import android.os.Parcelable
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.database.entity.DBNotice
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class APINotification(
    val id: String,
    val type: NotificationType,
    @Json(name = "created_at") val createdAt: String,
    val account: APIAccount,
    val status: APIStatus? = null
) : Parcelable {
    fun asDatabaseModel(noticeKind: Int): DBNotice {
        val notice = MastodonApiManager.moshi
            .adapter<APINotification>(APINotification::class.java).toJson(this)
        return DBNotice(0, noticeKind, notice)
    }
}

enum class NotificationType {
    @Json(name = "follow")
    FOLLOW,
    @Json(name = "favourite")
    FAVOURITE,
    @Json(name = "reblog")
    REBLOG,
    @Json(name = "mention")
    MENTION,
    @Json(name = "poll")
    POLL,
    @Json(name = "undefined")
    UNDEFINED;
}

