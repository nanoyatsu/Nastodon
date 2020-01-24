package com.nanoyatsu.nastodon.data.api.entity


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
    val type: String,
    @Json(name = "created_at") val created_at: String,
    val account: APIAccount,
    val status: APIStatus? = null
) : Parcelable

// パラメータ文字列以上の情報があり、実装箇所がちょっとつらい todo networkModel, domainModelの分離
enum class NotificationType(val descriptionId: Int, val icon: NoticeIcon) {
    FOLLOW(R.string.noticeDescriptionFollow, NoticeIcon.FOLLOW),
    FAVOURITE(R.string.noticeDescriptionFavourite, NoticeIcon.FAVOURITE),
    REBLOG(R.string.noticeDescriptionReblog, NoticeIcon.REBLOG),
    MENTION(R.string.noticeDescriptionMention, NoticeIcon.MENTION),
    POLL(R.string.noticeDescriptionPoll, NoticeIcon.POLL);
    // UNDEFINED(R.string.noticeDescriptionUndefined) // domainModel化の時にこうしたい

    val value get() = this.name.toLowerCase(Locale.ROOT)
}

