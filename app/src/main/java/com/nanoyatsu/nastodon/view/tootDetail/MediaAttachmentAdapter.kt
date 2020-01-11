package com.nanoyatsu.nastodon.view.tootDetail

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.data.api.entity.Attachment
import com.nanoyatsu.nastodon.data.api.entity.MediaType
import com.nanoyatsu.nastodon.databinding.ItemThumbnailBinding

class MediaAttachmentAdapter(
    private val context: Context,
    private val attachments: Array<Attachment>
) :
    RecyclerView.Adapter<MediaAttachmentAdapter.ViewHolder>() {
    override fun getItemCount(): Int = attachments.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(attachments[position])
    }

    class ViewHolder(val binding: ItemThumbnailBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding =
                    ItemThumbnailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(attachment: Attachment) {
            // まず画像だけ対応
            if (attachment.type == MediaType.IMAGE.toLower())
                Glide.with(binding.image)
                    .load(attachment.preview_url)
                    .apply {
                        RequestOptions()
                            .placeholder(R.drawable.loading_animation)
                            .error(R.drawable.ic_broken_image)
                    }
                    .into(binding.image)
        }
    }
}