package com.nanoyatsu.nastodon.model

data class Apps(
    val id: String,
    val name: String,
    val website: String?,
    val redirect_uri: String,
    val client_id: String,
    val client_secret: String
)

//{
//    "id": "",
//    "name": "Nastodon",
//    "website": null,
//    "redirect_uri": "urn:ietf:wg:oauth:2.0:oob",
//    "client_id": "",
//    "client_secret": ""
//}