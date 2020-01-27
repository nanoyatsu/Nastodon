package com.nanoyatsu.nastodon.data.api.entity

import com.nanoyatsu.nastodon.data.entity.Application
import com.squareup.moshi.Json

data class APIApplication(
    val name: String,
    val website: String?,
    val vapidKey: String?, // Added in 2.8.0
    @Json(name = "redirect_uri") val redirectUri: String?,
    @Json(name = "client_id") val clientId: String? = null,
    @Json(name = "client_secret") val clientSecret: String? = null
) {
    fun asDomainModel() = Application(name, website, vapidKey, redirectUri, clientId, clientSecret)
}