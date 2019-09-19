package com.nanoyatsu.nastodon.presenter

import com.nanoyatsu.nastodon.model.Token
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HTTP

interface MastodonApi {
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
}