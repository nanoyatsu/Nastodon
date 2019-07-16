package com.nanoyatsu.nastodon.model

data class Status(
    val id: String,
    val uri: String,
    val url: String?,
    val account: Account,
    val inReplyToId: String?,
    val inReplyToAccountId: String?,
    val reblog: String?,
    val content: String,
    val createdAt: String,
//    val emojis:Array<Emoji>
    val repliesCount: Int,
    val reblogsCount: Int,
    val favouritesCount: Int,
    val reblogged: Boolean?,
    val favourited: Boolean?,
    val muted: Boolean?,
    val sensitive: Boolean,
    val spoilerText: String,
    val visibility: String,
//    val mediaAttachments: Array<Attachment>,
//    val mentions: Array<Mention>,
//    val tags: Array<Tag>,
//    val card: Card?,
//    val poll: Poll?
//    val application: Application,
    val language: String?,
    val pinned: Boolean?
)

enum class Visibility { public, unlisted, private, direct, }
