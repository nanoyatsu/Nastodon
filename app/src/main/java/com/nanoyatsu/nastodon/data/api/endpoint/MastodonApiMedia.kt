package com.nanoyatsu.nastodon.data.api.endpoint

import com.nanoyatsu.nastodon.data.api.entity.APIAttachment
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST

interface MastodonApiMedia {
    @Multipart
    @POST("api/v1/media")
    suspend fun media(
        @Header("Authorization") authorization: String,
        @Body mediaBody: RequestBody
    ): Response<APIAttachment>

}