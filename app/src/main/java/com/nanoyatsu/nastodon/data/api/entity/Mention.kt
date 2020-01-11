package com.nanoyatsu.nastodon.data.api.entity

data class Mention(
    val id: String,
    val username: String,
    val acct: String, // @インスタンス名が含まれる表示名
    val url: String
)