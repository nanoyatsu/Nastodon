package com.nanoyatsu.nastodon.data.api.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Notification(
    val id: String,
    val type: String,
    val created_at: String,
    val account: Account,
    val status: Status? = null
) : Parcelable

enum class NotificationType { follow, facourite, reblog, mention, poll }

