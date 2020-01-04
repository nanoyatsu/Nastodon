package com.nanoyatsu.nastodon.data.api.endpoint

import com.nanoyatsu.nastodon.data.api.entity.Notification
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MastodonApiNotifications {
    @GET("/api/v1/notifications")
    suspend fun getAllNotifications(
        @Header("Authorization") authorization: String,
        @Query("max_id") maxId: String? = null,
        @Query("since_id") sinceId: String? = null,
        @Query("min_id") minId: String? = null,
        @Query("limit") limit: String? = null,
        @Query("exclude_types") excludeTypes: List<String>? = null,
        @Query("account_id") accountId: String? = null
    ): Response<List<Notification>>

    // todo GET /api/v1/notifications/:id
    // todo POST /api/v1/notifications/clear
    // todo POST /api/v1/notifications/:id/dismiss
}