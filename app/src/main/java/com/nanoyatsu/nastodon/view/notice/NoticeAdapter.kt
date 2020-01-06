package com.nanoyatsu.nastodon.view.notice

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.data.api.entity.Notification
import com.nanoyatsu.nastodon.data.api.entity.NotificationType
import com.nanoyatsu.nastodon.databinding.ItemNoticeBinding
import com.nanoyatsu.nastodon.resource.NoticeIcon

class NoticeAdapter(private val context: Context) :
    PagedListAdapter<Notification, NoticeAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notice = getItem(position)
        holder.bind(context, notice!!)
    }

    class ViewHolder(val binding: ItemNoticeBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding =
                    ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
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

    companion object {
        class DiffCallback : DiffUtil.ItemCallback<Notification>() {
            override fun areItemsTheSame(oldItem: Notification, newItem: Notification) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Notification, newItem: Notification) =
                oldItem == newItem
        }
    }
}