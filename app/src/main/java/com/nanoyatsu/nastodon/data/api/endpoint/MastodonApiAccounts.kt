package com.nanoyatsu.nastodon.data.api.endpoint

import com.nanoyatsu.nastodon.data.api.entity.APIAccount
import com.nanoyatsu.nastodon.data.api.entity.APIRelationship
import com.nanoyatsu.nastodon.data.api.entity.APIStatus
import retrofit2.Response
import retrofit2.http.*

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
        @Query("max_id") maxId: String? = null, // returns are older than ID
        @Query("since_id") sinceId: String? = null, // returns are newer than ID
        @Query("limit") limit: Int? = null // default 40
    ): Response<List<APIAccount>>

    // GET /api/v1/accounts/:id/following
    @GET("api/v1/accounts/{id}/following")
    suspend fun getFollowingById(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Query("max_id") maxId: String? = null, // returns are older than ID
        @Query("since_id") sinceId: String? = null, // returns are newer than ID
        @Query("limit") limit: Int? = null // default 40
    ): Response<List<APIAccount>>

    @GET("api/v1/accounts/{id}/statuses")
    suspend fun getToots(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        // review 以下 ドキュメントに記載が無いが、おそらく存在する(/api/v1/timelines/homeを参考)
        @Query("max_id") maxId: String? = null, // returns are older than ID
        @Query("since_id") sinceId: String? = null, // returns are newer than ID
        @Query("min_id") minId: String? = null, // returns are immediately newer than ID
        @Query("limit") limit: Int? = null // default 20
    ): Response<List<APIStatus>>

    @POST("api/v1/accounts/{id}/follow")
    suspend fun follow(
        @Header("Authorization") authorization: String,
        @Path("id") id: String // value暫定
    ): Response<APIRelationship>

    @POST("api/v1/accounts/{id}/unfollow")
    suspend fun unFollow(
        @Header("Authorization") authorization: String,
        @Path("id") id: String // value暫定
    ): Response<APIRelationship>


    //クエリは ?id[]=1&id[]=2 の形で指定する
    @GET("api/v1/accounts/relationships")
    suspend fun getRelationships(
        @Header("Authorization") authorization: String,
        @Query(value = "id[]") id: String // value暫定
    ): Response<List<APIRelationship>>

    // todo GET /api/v1/accounts/search
}