package com.nanoyatsu.nastodon.data.api.endpoint

import com.nanoyatsu.nastodon.data.api.entity.Apps
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface MastodonApiApps {
    // POST /api/v1/apps
    @POST("api/v1/apps")
    suspend fun getClientId(@Body appsBody: AppsBody = AppsBody()): Response<Apps>

    data class AppsBody(
        val client_name: String = "Nastodon",
        // val redirect_uris: String = "urn:ietf:wg:oauth:2.0:oob",
        val redirect_uris: String = "mastodon://nastodon",
        val scopes: String = "read write follow"
    )

    // GET /api/v1/apps/verify_credentials
    @GET("api/v1/apps/verify_credentials")
    suspend fun verifyCredentials(@Header("Authorization") authorization: String): Response<Apps>
}