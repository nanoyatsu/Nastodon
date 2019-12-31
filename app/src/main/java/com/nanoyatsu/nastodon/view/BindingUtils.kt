package com.nanoyatsu.nastodon.view

import android.os.Build
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nanoyatsu.nastodon.R
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .circleCrop()
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .into(imgView)
    }
}

@BindingAdapter("tootText")
fun bindTootText(view: TextView, text: String?) {
    text?.let {
        view.text = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
    }
}

@BindingAdapter("createdAt")
fun bindTimeText(view: TextView, text: String?) {
    text?.let {
        val timeText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val createdAt = Instant.parse(text).atZone(ZoneId.of("JST"))
            val now = Instant.now().atZone(ZoneId.of("JST"))
            val betweenSec = ChronoUnit.SECONDS.between(createdAt, now)
            when {
                (betweenSec < 60) -> "${betweenSec}秒前"
                (betweenSec < 60 * 60) -> "${betweenSec / 60}分前"
                (betweenSec < 60 * 60 * 24) -> "${betweenSec / 60 / 24}時間前"
                else -> createdAt.format(DateTimeFormatter.ofPattern("MM/dd"))
            }
        } else {
            val createdAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.JAPAN).parse(text)!!
            val now = Date()
            val betweenSec = (now.time - createdAt.time) / TimeUnit.SECONDS.toMillis(1)
            when {
                (betweenSec < 60) -> "${betweenSec}秒前"
                (betweenSec < 60 * 60) -> "${betweenSec / 60}分前"
                (betweenSec < 60 * 60 * 24) -> "${betweenSec / 60 / 24}時間前"
                else -> SimpleDateFormat("MM/dd", Locale.JAPAN).format(createdAt)
            }
        }

        view.text = timeText
    }
}