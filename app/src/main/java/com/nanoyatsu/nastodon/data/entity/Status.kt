package com.nanoyatsu.nastodon.data.entity

import android.os.Parcelable
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Status(
    val id: String,
    val uri: String,
    val url: String?,
    val account: Account,
    @Json(name = "in_reply_to_id") val inReplyToId: String?,
    @Json(name = "in_reply_to_account_id") val inReplyToAccountId: String?,
    val reblog: Status?,
    val content: String,
    @Json(name = "created_at") val createdAt: String,
//    val emojis:Array<Emoji>
    @Json(name = "replies_count") var repliesCount: Int?, // Added in 2.5.0
    @Json(name = "reblogs_count") var reblogsCount: Int,
    @Json(name = "favourites_count") var favouritesCount: Int,
    var reblogged: Boolean?,
    var favourited: Boolean?,
    val muted: Boolean?,
    val sensitive: Boolean,
    @Json(name = "spoiler_text") val spoilerText: String,
    val visibility: String,
    @Json(name = "media_attachments") val mediaAttachments: List<Attachment>,
//    val mentions: Array<Mention>,
//    val tags: Array<Tag>,
//    val card: Card?,
//    val poll: Poll?
//    val application: Application,
    val language: String?,
    val pinned: Boolean?
) : Parcelable {
    fun asDatabaseModel() =
        MastodonApiManager.moshi.adapter<Status>(Status::class.java).toJson(this)
}

enum class Visibility(val label: String) {
    @Json(name = "public")
    PUBLIC("公開"),
    @Json(name = "unlisted")
    UNLISTED("未収載"),
    @Json(name = "private")
    PRIVATE("非公開"),
    @Json(name = "direct")
    DIRECT("ダイレクト"),
}
