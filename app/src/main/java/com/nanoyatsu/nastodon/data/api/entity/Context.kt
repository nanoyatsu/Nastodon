package com.nanoyatsu.nastodon.data.api.entity

// tree around a given status
// トゥートの連結ツリーを表現する型
data class Context(
    val ancestors: List<APIStatus>,
    val descendants: List<APIStatus>
)