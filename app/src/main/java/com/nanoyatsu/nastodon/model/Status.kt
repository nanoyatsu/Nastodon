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
    var repliesCount: Int,
    var reblogsCount: Int,
    var favouritesCount: Int,
    var reblogged: Boolean?,
    var favourited: Boolean?,
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

enum class Visibility(val label: String) {
    PUBLIC("公開"),
    UNLISTED("未収載"),
    PRIVATE("非公開"),
    DIRECT("ダイレクト"),
}
