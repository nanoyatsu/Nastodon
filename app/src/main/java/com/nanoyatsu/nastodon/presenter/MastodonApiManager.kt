package com.nanoyatsu.nastodon.presenter

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MastodonApiManager(baseUrl: String) {
    val api :MastodonApi

    init {
        val gson = GsonBuilder().let {
            it.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            it.create()
        }
        val retrofit = Retrofit.Builder().let {
            it.baseUrl(baseUrl)
            it.addConverterFactory(GsonConverterFactory.create(gson))
            it.build()
        }
        api = retrofit.create(MastodonApi::class.java)
    }
}