package com.nanoyatsu.nastodon.data.api.entity

import com.nanoyatsu.nastodon.data.domain.Relationship
import com.squareup.moshi.Json

// relationship between accounts, such as following / blocking / muting / etc.
// アカウント間の関係 フォローとかブロックとかミュートとか
data class APIRelationship(
    val id: String,
    val following: Boolean,
    val requested: Boolean, // フォローリクエスト中かどうか
    val endorsed: Boolean?, // Added in 2.5.0
    @Json(name = "followed_by") val followedBy: Boolean,
    val muting: Boolean,
    @Json(name = "muting_notifications") val mutingNotifications: Boolean,
    @Json(name = "showing_reblogs") val showingReblogs: Boolean,
    val blocking: Boolean,
    @Json(name = "domain_blocking") val domainBlocking: Boolean, // you blocking this user's domain
    val blockedBy: Boolean? // Added in 2.8.0 // this user is blocking you
) {
    fun asDomainModel() = Relationship(
        id,
        following,
        requested,
        endorsed,
        followedBy,
        muting,
        mutingNotifications,
        showingReblogs,
        blocking,
        domainBlocking,
        blockedBy
    )

}