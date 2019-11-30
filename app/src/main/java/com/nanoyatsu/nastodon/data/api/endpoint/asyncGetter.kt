package com.nanoyatsu.nastodon.data.api.endpoint

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import okhttp3.OkHttpClient
import okhttp3.Request

public fun getImageAsync(url: String): Deferred<Bitmap> {
// 画像はGlideに任せてしまったのでそのうち削除
    return GlobalScope.async {
        val request = Request.Builder().let {
            it.url(url)
            it.get()
            it.build()
        }

        val response = OkHttpClient().newCall(request).execute() // todo 異常系
        BitmapFactory.decodeStream(response.body()?.byteStream())
    }
}
