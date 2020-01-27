package com.nanoyatsu.nastodon.data.domain

data class Token(
    val accessToken: String,
    val tokenType: String,
    val scope: String,
    val createdAt: Int
)