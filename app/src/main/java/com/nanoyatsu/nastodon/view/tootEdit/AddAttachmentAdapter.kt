package com.nanoyatsu.nastodon.view.tootEdit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.data.domain.Attachment
import com.nanoyatsu.nastodon.databinding.ItemAttachmentAdderBinding

class AddAttachmentAdapter :
    ListAdapter<Attachment, AddAttachmentAdapter.ViewHolder>(DiffCallback()) {

    override fun getItemCount(): Int =
        super.getItemCount() + if (super.getItemCount() < LIMIT_SIZE) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (super.getItemCount() >= LIMIT_SIZE || position >= super.getItemCount())
            holder.bind(getItem(position))
    }

    class ViewHolder(val binding: ItemAttachmentAdderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemAttachmentAdderBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(attachment: Attachment) {}
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