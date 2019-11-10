package com.nanoyatsu.nastodon.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "auth_info")
data class AuthInfo(
    @PrimaryKey(autoGenerate = true) val index: Int,
    @ColumnInfo(name = "instance_url") val instanceUrl: String,
    @ColumnInfo(name = "client_id") val clientId: String,
    @ColumnInfo(name = "client_secret") val clientSecret: String,
    @ColumnInfo(name = "access_token") val accessToken: String,
    @ColumnInfo(name = "token_created_at") val tokenCreatedAt: Int,
    @ColumnInfo(name = "account_id") val accountId: String,
    @ColumnInfo(name = "account_username") val accountUsername: String,
    @ColumnInfo(name = "account_display_name") val accountDisplayName: String,
    @ColumnInfo(name = "account_avatar") val accountAvatar: String,
    @ColumnInfo(name = "account_header") val accountHeader: String
)