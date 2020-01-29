package com.nanoyatsu.nastodon.data.api.entity

import android.os.Parcelable
import com.nanoyatsu.nastodon.data.domain.Account
import com.nanoyatsu.nastodon.data.domain.Source
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class APIAccount(
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
    val moved: APIAccount?,
//    val fields:Array<Hash>?,
    val bot: Boolean?,
    val APISource: APISource?
//val token: Token?
) : Parcelable {
    fun asDomainModel(): Account = Account(
        id,
        username,
        acct,
        displayName,
        locked,
        createdAt,
        followersCount,
        followingCount,
        statusesCount,
        note,
        url,
        avatar,
        avatarStatic,
        header,
        headerStatic,
        moved?.asDomainModel(),
        bot,
        APISource?.asDomainModel()
    )

}

@Parcelize
data class APISource(
    val privacy: String?,
    val sensitive: Boolean?,
    val language: String?, // v2.4.2
    val note: String
//    val fields: Array<Hash> // v2.4.0
) : Parcelable {
    fun asDomainModel() = Source(privacy, sensitive, language, note)
}

//"account": {
//    "id": "45717",
//    "username": "nanoyatsu",
//    "acct": "nanoyatsu",
//    "display_name": "なのやつ",
//    "locked": false,
//    "created_at": "2018-08-31T16:29:54.926Z",
//    "note": "\u003cp\u003e謙虚さ…謙虚さだぞ\u003c/p\u003e",
//    "url": "https://qiitadon.com/@nanoyatsu",
//    "avatar": "https://file.qiitadon.com/accounts/avatars/000/045/717/original/a97f93c14f4348b42688bb72034b1d92.png",
//    "avatar_static": "https://file.qiitadon.com/accounts/avatars/000/045/717/original/a97f93c14f4348b42688bb72034b1d92.png",
//    "header": "https://file.qiitadon.com/accounts/headers/000/045/717/original/0d08b98cee437bdc.png",
//    "header_static": "https://file.qiitadon.com/accounts/headers/000/045/717/original/0d08b98cee437bdc.png",
//    "followers_count": 50,
//    "following_count": 25,
//    "statuses_count": 8382,
//    "qiita_username": "nanoyatsu"
//}

//accounts/verify_credentialsで帰ってくるやつとかはsourceがついてる (Returns Account with an extra source attribute.)
//{
//    "id": "45717",
//    "username": "nanoyatsu",
//    "acct": "nanoyatsu",
//    "display_name": "なのやつ",
//    "locked": false,
//    "created_at": "2018-08-31T16:29:54.926Z",
//    "note": "<p>謙虚さ…謙虚さだぞ</p>",
//    "url": "https://qiitadon.com/@nanoyatsu",
//    "avatar": "https://file.qiitadon.com/accounts/avatars/000/045/717/original/a97f93c14f4348b42688bb72034b1d92.png",
//    "avatar_static": "https://file.qiitadon.com/accounts/avatars/000/045/717/original/a97f93c14f4348b42688bb72034b1d92.png",
//    "header": "https://file.qiitadon.com/accounts/headers/000/045/717/original/0d08b98cee437bdc.png",
//    "header_static": "https://file.qiitadon.com/accounts/headers/000/045/717/original/0d08b98cee437bdc.png",
//    "followers_count": 55,
//    "following_count": 25,
//    "statuses_count": 8764,
//    "qiita_username": "nanoyatsu",
//    "source": {
//      "privacy": "public",
//      "sensitive": false,
//      "note": "謙虚さ…謙虚さだぞ"
//    }
//}