package com.nanoyatsu.nastodon.presenter

import com.nanoyatsu.nastodon.model.Apps
import com.nanoyatsu.nastodon.model.Status
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.Path
import retrofit2.http.Query

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

    @HTTP(method = "GET", path = "api/v1/statuses/{id}")
    public fun getTootById(@Path("id") id: String): Call<Status>

    @HTTP(method = "GET", path = "api/v1/timelines/public")
    public fun getPublicTimeline(
        @Query("local") local: Boolean? = null, // default false
        @Query("only_media") onlyMedia: Boolean? = null, // default false
        @Query("max_id") maxId: String? = null,
        @Query("since_id") sinceId: String? = null,
        @Query("min_id") minId: String? = null,
        @Query("limit") limit: Int? = null // default 20
    ): Call<Array<Status>>
}