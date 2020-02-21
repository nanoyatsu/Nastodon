package com.nanoyatsu.nastodon.view

import android.os.Build
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
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


//@BindingAdapter("android:layout_width")
@BindingAdapter("layout_width")
fun bindWidth(view: ImageView, width: Float) {
    val layoutParams: ViewGroup.LayoutParams = view.layoutParams
    layoutParams.width = width.toInt()
    view.layoutParams = layoutParams
}

//@BindingAdapter("android:layout_height")
@BindingAdapter("layout_height")
fun bindHeight(view: ImageView, height: Float) {
    val layoutParams: ViewGroup.LayoutParams = view.layoutParams
    layoutParams.height = height.toInt()
    view.layoutParams = layoutParams
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .into(imgView)
    }
}

@BindingAdapter("circleImageUrl")
fun bindCircleImage(imgView: ImageView, imgUrl: String?) {
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
        val plainText = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_DIV)
        val replaced = plainText.replace("""(\n)+$""".toRegex(), "") // 全体を覆うPタグによる改行が要らない
        view.text = replaced
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
                (betweenSec < 60 * 60 * 24) -> "${betweenSec / 60 / 60}時間前"
                else -> createdAt.format(DateTimeFormatter.ofPattern("MM/dd"))
            }
        } else {
            val createdAt =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.JAPAN).parse(text)!!
            val now = Date()
            val betweenSec = (now.time - createdAt.time) / TimeUnit.SECONDS.toMillis(1)
            when {
                (betweenSec < 60) -> "${betweenSec}秒前"
                (betweenSec < 60 * 60) -> "${betweenSec / 60}分前"
                (betweenSec < 60 * 60 * 24) -> "${betweenSec / 60 / 60}時間前"
                else -> SimpleDateFormat("MM/dd", Locale.JAPAN).format(createdAt)
            }
        }

        view.text = timeText
    }
}