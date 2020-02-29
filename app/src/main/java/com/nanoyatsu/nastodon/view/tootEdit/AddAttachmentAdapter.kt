package com.nanoyatsu.nastodon.view.tootEdit

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.nanoyatsu.nastodon.data.domain.Attachment

class AddAttachmentAdapter :
    ListAdapter<Attachment, AddAttachmentItemViewHolder>(DiffCallback()) {

    override fun getItemCount(): Int =
        super.getItemCount() + if (super.getItemCount() < LIMIT_SIZE) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddAttachmentItemViewHolder =
        AddAttachmentItemViewHolder.from(parent)

    override fun onBindViewHolder(holder: AddAttachmentItemViewHolder, position: Int) {
        if (super.getItemCount() >= LIMIT_SIZE || position >= super.getItemCount())
            holder.bind(getItem(position))
    }

    companion object {
        const val LIMIT_SIZE = 4

        class DiffCallback : DiffUtil.ItemCallback<Attachment>() {
            override fun areItemsTheSame(oldItem: Attachment, newItem: Attachment) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Attachment, newItem: Attachment) =
                oldItem == newItem
        }
    }
}