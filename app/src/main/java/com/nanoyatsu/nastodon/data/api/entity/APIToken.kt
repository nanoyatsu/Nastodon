package com.nanoyatsu.nastodon.data.api.entity

import com.nanoyatsu.nastodon.data.entity.Token
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class APIToken(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "token_type") val tokenType: String,
    val scope: String,
    @Json(name = "created_at") val createdAt: Int
) {
    fun asDomainModel() = Token(accessToken, tokenType, scope, createdAt)
}


//{
//    "access_token": "b5b1a9bcf5ede341a4b05cdd648e8a2b76bf9fd84ddc538c87ece0229542ae85",
//    "token_type": "bearer",
//    "scope": "read write follow",
//    "created_at": 1565801570
//}