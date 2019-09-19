package com.nanoyatsu.nastodon.presenter

import com.nanoyatsu.nastodon.model.Account
import retrofit2.Response
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface MastodonApiAccounts {
    // todo GET/api/v1/accounts/:id
    // todo POST /api/v1/accounts
    // todo GET /api/v1/accounts/verify_credentials
    @HTTP(method = "GET", path = "api/v1/accounts/verify_credentials")
    suspend fun verifyCredentials(
        @Header("Authorization") authorization: String): Response<Account>

    // todo PATCH /api/v1/accounts/update_credentials
    // GET /api/v1/accounts/:id/followers
    @HTTP(method = "GET", path = "api/v1/accounts/{id}/followers")
    suspend fun getFollowersById(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Query("limit") limit: Int? = null // default 40
    ): Response<Array<Account>>

    // GET /api/v1/accounts/:id/following
    @HTTP(method = "GET", path = "api/v1/accounts/{id}/following")
    suspend fun getFollowingById(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Query("limit") limit: Int? = null // default 40
    ): Response<Array<Account>>

    // todo GET /api/v1/accounts/:id/statuses
    // todo POST /api/v1/accounts/:id/follow
    // todo POST /api/v1/accounts/:id/unfollow
    // todo GET /api/v1/accounts/relationships
    // todo GET /api/v1/accounts/search
}