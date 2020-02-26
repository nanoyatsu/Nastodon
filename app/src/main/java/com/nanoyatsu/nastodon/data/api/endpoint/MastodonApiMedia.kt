package com.nanoyatsu.nastodon.data.api.endpoint

import com.nanoyatsu.nastodon.data.api.entity.APIAttachment
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MastodonApiMedia {
    @Multipart
    @POST("api/v1/media")
    suspend fun media(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part
    ): Response<APIAttachment>
}