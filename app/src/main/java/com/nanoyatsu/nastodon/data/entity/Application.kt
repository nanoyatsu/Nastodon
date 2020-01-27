package com.nanoyatsu.nastodon.data.entity

data class Application(
    val name: String,
    val website: String?,
    val vapidKey: String?, // Added in 2.8.0
    val redirectUri: String?,
    val clientId: String? = null,
    val clientSecret: String? = null
)