package com.nanoyatsu.nastodon.data.api.entity

// カスタム絵文字
data class Emoji(
    val shortcode: String,
    val url: String,
    val staticUrl: String,
    val visibleInPicker: Boolean,
    val category: String? // Added in 3.0.0
)