package com.nanoyatsu.nastodon.presenter

import com.nanoyatsu.nastodon.model.Apps
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.Header

interface MastodonApiApps {
    // POST /api/v1/apps
    @HTTP(method = "POST", path = "api/v1/apps", hasBody = true)
    suspend fun getClientId(@Body appsBody: AppsBody = AppsBody()): Response<Apps>

    data class AppsBody(
        val client_name: String = "Nastodon",
        // val redirect_uris: String = "urn:ietf:wg:oauth:2.0:oob",
        val redirect_uris: String = "mastodon://nastodon",
        val scopes: String = "read write follow"
    )

    // GET /api/v1/apps/verify_credentials
    @HTTP(method = "GET", path = "api/v1/apps/verify_credentials")
    suspend fun verifyCredentials(@Header("Authorization") authorization: String): Response<Apps>
}