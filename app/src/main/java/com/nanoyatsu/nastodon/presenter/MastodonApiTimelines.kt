package com.nanoyatsu.nastodon.presenter

import com.nanoyatsu.nastodon.model.Status
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MastodonApiTimelines {
    // todo GET /api/v1/timelines/home
    // todo GET /api/v1/conversations

    // GET /api/v1/timelines/public
    @GET("api/v1/timelines/public")
    suspend fun getPublicTimeline(
        @Query("local") local: Boolean? = null, // default false
        @Query("only_media") onlyMedia: Boolean? = null, // default false
        @Query("max_id") maxId: String? = null,
        @Query("since_id") sinceId: String? = null,
        @Query("min_id") minId: String? = null,
        @Query("limit") limit: Int? = null // default 20
    ): Response<Array<Status>>

    // todo GET /api/v1/timelines/tag/:hashtag
    // todo GET /api/v1/timelines/list/:list_id
}