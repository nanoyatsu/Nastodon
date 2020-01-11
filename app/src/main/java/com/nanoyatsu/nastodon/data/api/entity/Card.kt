package com.nanoyatsu.nastodon.data.api.entity

// for rich preview
data class Card(
    val url: String,
    val title: String,
    val description: String,
    val type: String,
    val author_name: String?,
    val author_url: String?,
    val provider_name: String?,
    val provider_url: String?,
    val html: String?,
    val width: Int?,
    val height: Int?,
    val image: String?,
    val embed_url: String?
)

enum class PreviewCardType { LINK, PHOTO, VIDEO, RICH }
//  - link = Link OEmbed
//  - photo = Photo OEmbed
//  - video = Video OEmbed
//  - rich = iframe OEmbed. Not currently accepted, so won't show up in practice.