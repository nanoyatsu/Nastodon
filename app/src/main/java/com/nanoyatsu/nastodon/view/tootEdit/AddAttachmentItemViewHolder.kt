package com.nanoyatsu.nastodon.view.tootEdit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.data.domain.Attachment
import com.nanoyatsu.nastodon.databinding.ItemAttachmentAdderBinding

class AddAttachmentItemViewHolder(val binding: ItemAttachmentAdderBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): AddAttachmentItemViewHolder {
            val binding =
                ItemAttachmentAdderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return AddAttachmentItemViewHolder(
                binding
            )
        }
    }

    fun bind(attachment: Attachment) {}
}