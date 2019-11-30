package com.nanoyatsu.nastodon.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "auth_info")
data class AuthInfo(
    @PrimaryKey(autoGenerate = true) val index: Int = 0,
    @ColumnInfo(name = "instance_url") var instanceUrl: String = "",
    @ColumnInfo(name = "client_id") var clientId: String = "",
    @ColumnInfo(name = "client_secret") var clientSecret: String = "",
    @ColumnInfo(name = "access_token") var accessToken: String = "",
    @ColumnInfo(name = "token_created_at") var tokenCreatedAt: Int = 0,
    @ColumnInfo(name = "account_id") var accountId: String = "",
    @ColumnInfo(name = "account_username") var accountUsername: String = "",
    @ColumnInfo(name = "account_display_name") var accountDisplayName: String = "",
    @ColumnInfo(name = "account_avatar") var accountAvatar: String = "",
    @ColumnInfo(name = "account_header") var accountHeader: String = ""
)