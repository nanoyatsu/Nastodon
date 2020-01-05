package com.nanoyatsu.nastodon.data.api.entity

import android.os.Parcelable
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

enum class NotificationType {
    FOLLOW, FAVOURITE, REBLOG, MENTION, POLL;

    val value get() = this.name.toLowerCase(Locale.ROOT)
}

