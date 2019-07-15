package com.nanoyatsu.nastodon.presenter

import com.nanoyatsu.nastodon.model.Status
import retrofit2.Call
import retrofit2.http.GET

interface ApiClient{
    @GET("api/v1/statuses/100645800762440207")
    public fun getNanoFirstToot(): Call<Status>
}