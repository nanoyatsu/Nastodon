package com.nanoyatsu.nastodon.data.api.endpoint

import com.nanoyatsu.nastodon.data.api.entity.Token
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MastodonApi {
    // 認証
    @POST("oauth/token")
    suspend fun getAccessToken(@Body tokenBody: TokenBody): Response<Token>

    data class TokenBody(
        val client_id: String,
        val client_secret: String,
        val redirect_uri: String = "mastodon://nastodon",
        val grant_type: String = "authorization_code",
        val code: String
    )
}