package com.nanoyatsu.nastodon.data.domain

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Account(
    val id: String,
    val username: String,
    val acct: String,
    @Json(name = "display_name") val displayName: String,
    val locked: Boolean,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "followers_count") val followersCount: Int,
    @Json(name = "following_count") val followingCount: Int,
    @Json(name = "statuses_count") val statusesCount: Int,
    val note: String,
    val url: String,
    val avatar: String,
    @Json(name = "avatar_static") val avatarStatic: String,
    val header: String,
    @Json(name = "header_static") val headerStatic: String,
//    val emojis:Array<Emoji>,
    val moved: Account?,
//    val fields:Array<Hash>?,
    val bot: Boolean?,
    val source: Source?
//val token: Token?
) : Parcelable

@Parcelize
data class Source(
    val privacy: String?,
    val sensitive: Boolean?,
    val language: String?, // v2.4.2
    val note: String
//    val fields: Array<Hash> // v2.4.0
) : Parcelable