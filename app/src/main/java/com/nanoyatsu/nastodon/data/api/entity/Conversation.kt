package com.nanoyatsu.nastodon.data.api.entity

// DMを表現する
// Added in 2.6.0
data class Conversation(
    val id: String,
    val accounts: List<APIAccount>,
    val unread: Boolean,
    val lastStatus: APIStatus?
)