package com.nanoyatsu.nastodon.presenter

import com.nanoyatsu.nastodon.model.Status
import retrofit2.Response
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface MastodonApiStatuses {
    // そのうち消す
    @HTTP(method = "GET", path = "api/v1/statuses/100645800762440207")
    suspend fun getNanoFirstToot(): Response<Status>

    // GET /api/v1/statuses/:id
    @HTTP(method = "GET", path = "api/v1/statuses/{id}")
    suspend fun getTootById(@Path("id") id: String): Response<Status>

    // todo GET /api/v1/statuses/:id/context
    // todo GET /api/v1/statuses/:id/card
    // todo GET /api/v1/statuses/:id/reblogged_by
    // todo GET /api/v1/statuses/:id/favourited_by

    // POST /api/v1/statuses
    @HTTP(method = "POST", path = "api/v1/statuses", hasBody = true)
    suspend fun postToot(
        @Header("Authorization") authorization: String,
        @Query("status") status: String,
        @Query("in_reply_to_id") inReplyToId: String? = null,
        @Query("media_ids") mediaIds: String? = null,
        @Query("poll") poll: String? = null,
        @Query("sensitive") sensitive: String? = null,
        @Query("spoiler_text") spoilerText: String? = null,
        @Query("visibility") visibility: String, // direct, private, unlisted, public
        @Query("scheduled_at") scheduledAt: String? = null,
        @Query("language") language: String? = null
    ): Response<Status>

    // todo DELETE /api/v1/statuses/:id
    // todo POST /api/v1/statuses/:id/reblog
    // todo POST /api/v1/statuses/:id/unreblog
    // todo POST /api/v1/statuses/:id/pin
    // todo POST /api/v1/statuses/:id/unpin

}