package com.nanoyatsu.nastodon.data.api.endpoint

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MastodonApiNotifications {
    enum class NotificationType { follow, facourite, reblog, mention, poll }

    @GET
    suspend fun getAllNotifications(
        @Header("Authorization") authorization: String,
        @Query("max_id") maxId: String? = null,
        @Query("since_id") sinceId: String? = null,
        @Query("min_id") minId: String? = null,
        @Query("limit") limit: String? = null,
        @Query("exclude_types") excludeTypes: Array<String>? = null,
        @Query("account_id") accountId: String? = null
    )

    // todo GET /api/v1/notifications/:id
    // todo POST /api/v1/notifications/clear
    // todo POST /api/v1/notifications/:id/dismiss
}