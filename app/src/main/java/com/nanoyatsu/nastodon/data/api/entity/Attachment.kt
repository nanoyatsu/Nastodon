package com.nanoyatsu.nastodon.data.api.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Attachment(
    val id: String,
    val type: String,
    val url: String,
    val preview_url: String,
    val remote_url: String?,
    val text_url: String?,
    // val meta:
    val description: String?,
    val blurhash: String? // added in 2.8.1
) : Parcelable

enum class MediaType() {
    UNKNOWN, IMAGE, GIFV, VIDEO, AUDIO
}
//    - unknown = unsupported or unrecognized file type
//    - image = Static image
//    - gifv = Looping, soundless animation
//    - video = Video clip
//    - audio = Audio track
