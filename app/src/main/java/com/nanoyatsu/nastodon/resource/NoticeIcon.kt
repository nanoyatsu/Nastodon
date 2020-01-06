package com.nanoyatsu.nastodon.resource

import com.nanoyatsu.nastodon.R

// 色に困ったところはとりあえずcolorPrimaryにしている
enum class NoticeIcon(val iconId: Int, val colorId: Int) {
    FOLLOW(R.drawable.ic_person_add_white, R.color.colorPrimary),
    FAVOURITE(R.drawable.ic_star_white, R.color.fav_yellow),
    REBLOG(R.drawable.ic_repeat_white, R.color.reblog_blue),
    MENTION(R.drawable.ic_reply_white, R.color.colorPrimary),
    POLL(R.drawable.ic_poll_white, R.color.colorPrimary),
    UNDEFINED(R.drawable.ic_error_white, R.color.error_red),
}