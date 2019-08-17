package com.nanoyatsu.nastodon.model

data class Token(
    val accessToken: String,
    val tokenType: String,
    val scope: String,
    val createdAt: Int
)

//{
//    "access_token": "b5b1a9bcf5ede341a4b05cdd648e8a2b76bf9fd84ddc538c87ece0229542ae85",
//    "token_type": "bearer",
//    "scope": "read write follow",
//    "created_at": 1565801570
//}