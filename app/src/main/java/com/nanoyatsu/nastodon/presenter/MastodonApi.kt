package com.nanoyatsu.nastodon.presenter

import com.nanoyatsu.nastodon.model.Account
import com.nanoyatsu.nastodon.model.Apps
import com.nanoyatsu.nastodon.model.Status
import com.nanoyatsu.nastodon.model.Token
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

// todo ? review APIのディレクトリごとにクラスを分ける？
interface MastodonApi {
    // そのうち消す
    @HTTP(method = "GET", path = "api/v1/statuses/100645800762440207")
    suspend fun getNanoFirstToot(): Response<Status>

    // 認証
    @HTTP(method = "POST", path = "api/v1/apps", hasBody = true)
    suspend fun getClientId(@Body appsBody: AppsBody = AppsBody()): Response<Apps>

    data class AppsBody(
        val client_name: String = "Nastodon",
//        val redirect_uris: String = "urn:ietf:wg:oauth:2.0:oob",
        val redirect_uris: String = "mastodon://nastodon",
        val scopes: String = "read write follow"
    )

    // verify_credentials
    @HTTP(method = "GET", path = "api/v1/apps/verify_credentials")
    suspend fun verifyCredentials(@Header("Authorization") authorization: String): Response<Apps>

    // 認証
    @HTTP(method = "POST", path = "oauth/token", hasBody = true)
    suspend fun getAccessToken(@Body tokenBody: TokenBody): Response<Token>

    data class TokenBody(
        val client_id: String,
        val client_secret: String,
        val redirect_uri: String = "mastodon://nastodon",
        val grant_type: String = "authorization_code",
        val code: String
    )

    @HTTP(method = "GET", path = "api/v1/statuses/{id}")
    suspend fun getTootById(@Path("id") id: String): Response<Status>

    @HTTP(method = "GET", path = "api/v1/timelines/public")
    suspend fun getPublicTimeline(
        @Query("local") local: Boolean? = null, // default false
        @Query("only_media") onlyMedia: Boolean? = null, // default false
        @Query("max_id") maxId: String? = null,
        @Query("since_id") sinceId: String? = null,
        @Query("min_id") minId: String? = null,
        @Query("limit") limit: Int? = null // default 20
    ): Response<Array<Status>>

    @HTTP(method = "POST", path = "api/v1/statuses", hasBody = true)
    suspend fun postToot(
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
    ): Response<Status>

    @HTTP(method = "GET", path = "api/v1/accounts/{id}/following")
    suspend fun getFollowingBy(
        @Path("id") id: String,
        @Query("limit") limit: Int? = null // default 40
    ): Response<Array<Account>>

    @HTTP(method = "GET", path = "api/v1/accounts/{id}/followers")
    suspend fun getFollowersBy(
        @Path("id") id: String,
        @Query("limit") limit: Int? = null // default 40
    ): Response<Array<Account>>

}