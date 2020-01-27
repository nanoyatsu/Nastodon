package com.nanoyatsu.nastodon.view.notice

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.data.entity.Notification
import com.nanoyatsu.nastodon.data.entity.Status
import com.nanoyatsu.nastodon.databinding.ItemNoticeBinding
import com.nanoyatsu.nastodon.resource.NoticeIcon

class NoticeItemViewHolder(val binding: ItemNoticeBinding, private val navigation: Navigation?) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup, navigation: Navigation?): NoticeItemViewHolder {
            val binding =
                ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return NoticeItemViewHolder(binding, navigation)
        }
    }

    fun bind(context: Context, notice: Notification) {
        binding.notice = notice

        val descriptionId = notice.type.descriptionId
        binding.description.text = context.getString(descriptionId, notice.account.displayName)
        val icon: NoticeIcon = notice.type.icon
        binding.typeIcon.background =
            context.getDrawable(icon.iconId)?.apply { setTint(context.getColor(icon.colorId)) }
    }

    interface Navigation {
        fun transTootDetail(toot: Status)
    }
}
