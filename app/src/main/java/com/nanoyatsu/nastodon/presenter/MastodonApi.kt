package com.nanoyatsu.nastodon.presenter

import com.nanoyatsu.nastodon.model.Status
import retrofit2.Call
import retrofit2.http.HTTP
import retrofit2.http.Path
import retrofit2.http.Query

interface MastodonApi {
    @HTTP(method = "GET", path = "api/v1/statuses/100645800762440207")
    public fun getNanoFirstToot(): Call<Status>

    @HTTP(method = "GET", path = "api/v1/statuses/{id}")
    public fun getTootById(@Path("id")id:String): Call<Status>

    @HTTP(method = "GET", path = "api/v1/timelines/public")
    public fun getPublicTimeline(
        @Query("local") local: Boolean? = null, // default false
        @Query("only_media") onlyMedia: Boolean? = null, // default false
        @Query("max_id") maxId: String? = null,
        @Query("since_id") sinceId: String? = null,
        @Query("min_id") minId: String? = null,
        @Query("limit") limit: Int? = null // default 20
    ): Call<Array<Status>>
}