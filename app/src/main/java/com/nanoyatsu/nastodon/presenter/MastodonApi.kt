package com.nanoyatsu.nastodon.presenter

import com.nanoyatsu.nastodon.model.Apps
import com.nanoyatsu.nastodon.model.Status
import com.nanoyatsu.nastodon.model.Token
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface MastodonApi {
    // そのうち消す
    @HTTP(method = "GET", path = "api/v1/statuses/100645800762440207")
    public fun getNanoFirstToot(): Call<Status>

    // 認証
    @HTTP(method = "POST", path = "api/v1/apps", hasBody = true)
    public fun getClientId(@Body appsBody: AppsBody = AppsBody()): Call<Apps>

    data class AppsBody(
        val client_name: String = "Nastodon",
//        val redirect_uris: String = "urn:ietf:wg:oauth:2.0:oob",
        val redirect_uris: String = "mastodon://nastodon",
        val scopes: String = "read write follow"
    )

    // 認証
    @HTTP(method = "POST", path = "oauth/token", hasBody = true)
    public fun getAccessToken(@Body tokenBody: TokenBody): Call<Token>

    data class TokenBody(
        val client_id: String,
        val client_secret: String,
        val redirect_uri: String = "mastodon://nastodon",
        val grant_type: String = "authorization_code",
        val code: String
    )

    @HTTP(method = "GET", path = "api/v1/statuses/{id}")
    public fun getTootById(@Path("id") id: String): Call<Status>

    @HTTP(method = "GET", path = "api/v1/timelines/public")
    public suspend fun getPublicTimeline(
        @Query("local") local: Boolean? = null, // default false
        @Query("only_media") onlyMedia: Boolean? = null, // default false
        @Query("max_id") maxId: String? = null,
        @Query("since_id") sinceId: String? = null,
        @Query("min_id") minId: String? = null,
        @Query("limit") limit: Int? = null // default 20
    ): Response<Array<Status>>

    @HTTP(method = "POST", path = "api/v1/statuses", hasBody = true)
    public fun postToot(
        @Header("Authorization") authorization: String,
        @Query("status") status: String,
        @Query("in_reply_to_id") inReplyToId: String? = null,
        @Query("media_ids") mediaIds: String? = null,
        @Query("poll") poll: String? = null,
        @Query("sensitive") sensitive: String? = null,
        @Query("spoiler_text") spoilerText: String? = null,
        @Query("visibility") visibility: String, // direct, private, unlisted, public
        @Query("scheduled_at") scheduledAt: String? = null,
        @Query("language") language: String? = null
    ): Call<Status>

}