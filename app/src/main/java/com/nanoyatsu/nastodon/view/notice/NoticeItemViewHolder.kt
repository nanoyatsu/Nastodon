package com.nanoyatsu.nastodon.view.notice

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.data.api.entity.Notification
import com.nanoyatsu.nastodon.data.api.entity.NotificationType
import com.nanoyatsu.nastodon.databinding.ItemNoticeBinding
import com.nanoyatsu.nastodon.resource.NoticeIcon

class NoticeItemViewHolder(val binding: ItemNoticeBinding) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): NoticeItemViewHolder {
            val binding =
                ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return NoticeItemViewHolder(binding)
        }
    }

    fun bind(context: Context, notice: Notification) {
        binding.notice = notice

        val type = NotificationType.values().firstOrNull { it.value == notice.type }

        // todo : Replyは別レイアウトにする
        val descriptionId: Int
        val icon: NoticeIcon
        if (type == null) {
            descriptionId = R.string.noticeDescriptionUndefined
            icon = NoticeIcon.UNDEFINED
        } else {
            descriptionId = type.descriptionId
            icon = type.icon
        }

        binding.description.text = context.getString(descriptionId, notice.account.displayName)
        binding.typeIcon.background =
            context.getDrawable(icon.iconId)?.apply { setTint(context.getColor(icon.colorId)) }
    }
}
