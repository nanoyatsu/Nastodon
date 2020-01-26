package com.nanoyatsu.nastodon.data.api.entity

import android.os.Parcelable
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.database.entity.DBStatus
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class APIStatus(
    val id: String,
    val uri: String,
    val url: String?,
    val account: APIAccount,
    @Json(name = "in_reply_to_id") val inReplyToId: String?,
    @Json(name = "in_reply_to_account_id") val inReplyToAccountId: String?,
    val reblog: APIStatus?,
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
    @Json(name = "media_attachments") val mediaAttachments: List<APIAttachment>,
//    val mentions: Array<Mention>,
//    val tags: Array<Tag>,
//    val card: Card?,
//    val poll: Poll?
//    val application: Application,
    val language: String?,
    val pinned: Boolean?
) : Parcelable {
    fun asDatabaseModel(timelineKind: Int): DBStatus {
        val status = MastodonApiManager.moshi.adapter<APIStatus>(APIStatus::class.java).toJson(this)
        return DBStatus(0, timelineKind, status)
    }
}

enum class Visibility(val label: String) {
    PUBLIC("公開"),
    UNLISTED("未収載"),
    PRIVATE("非公開"),
    DIRECT("ダイレクト"),
}
