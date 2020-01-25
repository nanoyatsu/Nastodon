package com.nanoyatsu.nastodon.data.api.endpoint

import com.nanoyatsu.nastodon.data.api.entity.APIStatus
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MastodonApiTimelines {
    // GET /api/v1/timelines/home
    @GET("/api/v1/timelines/home")
    suspend fun getHomeTimeline(
        @Header("Authorization") authorization: String,
        @Query("max_id") maxId: String? = null, // returns are older than ID
        @Query("since_id") sinceId: String? = null, // returns are newer than ID
        @Query("min_id") minId: String? = null, // returns are immediately newer than ID (?? todo 試す)
        @Query("limit") limit: Int? = null // default 20
    ): Response<List<APIStatus>>

    // todo GET /api/v1/conversations

    // GET /api/v1/timelines/public
    @GET("api/v1/timelines/public")
    suspend fun getPublicTimeline(
        @Header("Authorization") authorization: String? = null, // 認証無しでも取得できる
        @Query("local") local: Boolean? = null, // default false
        @Query("only_media") onlyMedia: Boolean? = null, // default false
        @Query("max_id") maxId: String? = null,
        @Query("since_id") sinceId: String? = null,
        @Query("min_id") minId: String? = null,
        @Query("limit") limit: Int? = null // default 20
    ): Response<List<APIStatus>>

    // todo GET /api/v1/timelines/tag/:hashtag
    // todo GET /api/v1/timelines/list/:list_id
}