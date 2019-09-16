package com.nanoyatsu.nastodon.presenter

import com.nanoyatsu.nastodon.model.Status
import retrofit2.Response
import retrofit2.http.HTTP

interface MastodonApiStatuses {
    // そのうち消す
    @HTTP(method = "GET", path = "api/v1/statuses/100645800762440207")
    suspend fun getNanoFirstToot(): Response<Status>


}