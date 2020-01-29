package com.nanoyatsu.nastodon.data.api.endpoint

import com.nanoyatsu.nastodon.data.api.entity.APIAccount
import com.nanoyatsu.nastodon.data.api.entity.APIStatus
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface MastodonApiAccounts {
    // todo GET/api/v1/accounts/:id
    // todo POST /api/v1/accounts
    // todo GET /api/v1/accounts/verify_credentials
    @GET("api/v1/accounts/verify_credentials")
    suspend fun verifyCredentials(
        @Header("Authorization") authorization: String
    ): Response<APIAccount>

    // todo PATCH /api/v1/accounts/update_credentials
    // GET /api/v1/accounts/:id/followers
    @GET("api/v1/accounts/{id}/followers")
    suspend fun getFollowersById(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Query("limit") limit: Int? = null // default 40
    ): Response<Array<APIAccount>>

    // GET /api/v1/accounts/:id/following
    @GET("api/v1/accounts/{id}/following")
    suspend fun getFollowingById(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Query("limit") limit: Int? = null // default 40
    ): Response<Array<APIAccount>>

    @GET("api/v1/accounts/:id/statuses")
    suspend fun getToots(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        // review 以下 ドキュメントに記載が無いが、おそらく存在する(/api/v1/timelines/homeを参考)
        @Query("max_id") maxId: String? = null, // returns are older than ID
        @Query("since_id") sinceId: String? = null, // returns are newer than ID
        @Query("min_id") minId: String? = null, // returns are immediately newer than ID
        @Query("limit") limit: Int? = null // default 20
    ): Response<List<APIStatus>>

    // todo POST /api/v1/accounts/:id/follow
    // todo POST /api/v1/accounts/:id/unfollow
    // todo GET /api/v1/accounts/relationships
    // todo GET /api/v1/accounts/search
}