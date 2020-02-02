package com.nanoyatsu.nastodon.data.domain

data class Relationship(
    val id: String,
    val following: Boolean,
    val requested: Boolean, // フォローリクエスト中かどうか
    val endorsed: Boolean?, // 本来require Added in 2.5.0
    val followedBy: Boolean,
    val muting: Boolean,
    val mutingNotifications: Boolean,
    val showingReblogs: Boolean,
    val blocking: Boolean,
    val domainBlocking: Boolean, // you blocking this user's domain
    val blockedBy: Boolean? // Added in 2.8.0 // this user is blocking you
)