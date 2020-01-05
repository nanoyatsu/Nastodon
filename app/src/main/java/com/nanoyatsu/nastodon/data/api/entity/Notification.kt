package com.nanoyatsu.nastodon.data.api.entity

import android.os.Parcelable
import com.nanoyatsu.nastodon.R
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Notification(
    val id: String,
    val type: String,
    val created_at: String,
    val account: Account,
    val status: Status? = null
) : Parcelable

// (特にメッセージを持つところの)実装箇所がちょっとつらい todo networkModel, domainModelの分離
enum class NotificationType(val descriptionId: Int) {
    FOLLOW(R.string.noticeDescriptionFollow),
    FAVOURITE(R.string.noticeDescriptionFavourite),
    REBLOG(R.string.noticeDescriptionReblog),
    MENTION(R.string.noticeDescriptionMention),
    POLL(R.string.noticeDescriptionPoll);
    // UNDEFINED(R.string.noticeDescriptionUndefined) // domainModel化の時にこうしたい

    val value get() = this.name.toLowerCase(Locale.ROOT)
}

