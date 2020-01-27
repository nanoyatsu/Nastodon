package com.nanoyatsu.nastodon.data.domain

import android.os.Parcelable
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.resource.NoticeIcon
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
data class Notification(
    val id: String,
    val type: NotificationType,
    @Json(name = "created_at") val created_at: String,
    val account: Account,
    val status: Status? = null
) : Parcelable

enum class NotificationType(val descriptionId: Int, val icon: NoticeIcon) {
    @Json(name = "follow")
    FOLLOW(R.string.noticeDescriptionFollow, NoticeIcon.FOLLOW),
    @Json(name = "favourite")
    FAVOURITE(R.string.noticeDescriptionFavourite, NoticeIcon.FAVOURITE),
    @Json(name = "reblog")
    REBLOG(R.string.noticeDescriptionReblog, NoticeIcon.REBLOG),
    @Json(name = "mention")
    MENTION(R.string.noticeDescriptionMention, NoticeIcon.MENTION),
    @Json(name = "poll")
    POLL(R.string.noticeDescriptionPoll, NoticeIcon.POLL),
    @Json(name = "undefined")
    UNDEFINED(R.string.noticeDescriptionUndefined, NoticeIcon.UNDEFINED);

    val value get() = this.name.toLowerCase(Locale.ROOT)
}

