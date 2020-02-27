package com.nanoyatsu.nastodon.data.api.endpoint

import com.nanoyatsu.nastodon.data.api.entity.APIStatus
import retrofit2.Response
import retrofit2.http.*

interface MastodonApiStatuses {
    // そのうち消す
    @GET("api/v1/statuses/100645800762440207")
    suspend fun getNanoFirstToot(): Response<APIStatus>

    // GET /api/v1/statuses/:id
    @GET("api/v1/statuses/{id}")
    suspend fun getTootById(@Path("id") id: String): Response<APIStatus>

    // todo GET /api/v1/statuses/:id/context
    // todo GET /api/v1/statuses/:id/card
    // todo GET /api/v1/statuses/:id/reblogged_by
    // todo GET /api/v1/statuses/:id/favourited_by

    // POST /api/v1/statuses
    @POST("api/v1/statuses")
    suspend fun postToot(
        @Header("Authorization") authorization: String,
        @Query("status") status: String,
        @Query("in_reply_to_id") inReplyToId: String? = null,
        @Query("poll") poll: String? = null, // added 2.8.0
        @Query("sensitive") sensitive: String? = null,
        @Query("spoiler_text") spoilerText: String? = null,
        @Query("visibility") visibility: String, // direct, private, unlisted, public
        @Query("scheduled_at") scheduledAt: String? = null,
        @Query("language") language: String? = null,
        @Query("media_ids[]") vararg mediaIds: String? = arrayOf()
    ): Response<APIStatus>

    @DELETE("/api/v1/statuses/{id}")
    suspend fun deleteToot(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Response<Unit> // v2.9.0 で返りが変則Statusになった

    // POST /api/v1/statuses/:id/reblog
    @POST("api/v1/statuses/{id}/reblog")
    suspend fun reblog(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Response<APIStatus>

    // POST /api/v1/statuses/:id/unreblog
    @POST("api/v1/statuses/{id}/unreblog")
    suspend fun unReblog(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Response<APIStatus>

    // POST /api/v1/statuses/:id/pin
    @POST("api/v1/statuses/{id}/pin")
    suspend fun pin(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Response<APIStatus>

    // POST /api/v1/statuses/:id/unpin
    @POST("api/v1/statuses/{id}/unpin")
    suspend fun unPin(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Response<APIStatus>

}