package com.nanoyatsu.nastodon.presenter

import com.nanoyatsu.nastodon.model.Account
import com.nanoyatsu.nastodon.model.Apps
import com.nanoyatsu.nastodon.model.Status
import com.nanoyatsu.nastodon.model.Token
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

// todo APIのディレクトリごとにクラスを分ける
interface MastodonApi {
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

    @HTTP(method = "GET", path = "api/v1/accounts/{id}/following")
    suspend fun getFollowingById(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Query("limit") limit: Int? = null // default 40
    ): Response<Array<Account>>

    @HTTP(method = "GET", path = "api/v1/accounts/{id}/followers")
    suspend fun getFollowersById(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Query("limit") limit: Int? = null // default 40
    ): Response<Array<Account>>

}