package com.nanoyatsu.nastodon.view.timeline

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.data.domain.Attachment
import com.nanoyatsu.nastodon.data.domain.Status
import com.nanoyatsu.nastodon.databinding.ItemTootBinding
import com.nanoyatsu.nastodon.view.tootDetail.MediaAttachmentAdapter
import com.nanoyatsu.nastodon.view.tootDetail.TootViewModel

class TimelineItemViewHolder(val binding: ItemTootBinding, private val navigation: Navigation?) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup, navigation: Navigation?): TimelineItemViewHolder {
            val binding = ItemTootBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return TimelineItemViewHolder(binding, navigation)
        }
    }

    fun bind(context: Context, toot: Status, apiManager: MastodonApiManager, auth: AuthInfo) {
        require(context is FragmentActivity)
        setupAttachments(context, binding.attachments, toot.mediaAttachments)

        val vm = TootViewModel(toot, auth, apiManager) //
        vm.timeClickEvent.observe(context, Observer { if (it) onTimeClick(vm) })
        vm.replyEvent.observe(context, Observer { if (it) onReplyClick(vm) })
        vm.reblogEvent.observe(context, Observer { if (it) vm.doReblog() })
        vm.favouriteEvent.observe(context, Observer { if (it) vm.doFav() })

        binding.vm = vm
        binding.lifecycleOwner = context

        binding.executePendingBindings()
    }

    private fun setupAttachments(context: Context, view: RecyclerView, contents: List<Attachment>) {
        view.layoutManager = GridLayoutManager(context, 2)
        view.adapter = MediaAttachmentAdapter(contents.toTypedArray())
            .apply {
                publicListener = object : MediaAttachmentAdapter.ThumbnailClickListener {
                    override val onThumbnailClick = { onAttachmentClick(contents) }
                }
            }
    }

    private fun onTimeClick(vm: TootViewModel) {
        navigation?.transTootDetail(vm.toot.value!!)
        vm.onTimeClickFinished()
    }

    private fun onReplyClick(vm: TootViewModel) {
        navigation?.transTootEditAsReply(vm.toot.value!!)
        vm.onReplyClickFinished()
    }

    private fun onAttachmentClick(contents: List<Attachment>) {
        navigation?.transImagePager(contents)
    }

    interface Navigation {
        fun transTootEditAsReply(toot: Status)
        fun transTootDetail(toot: Status)
        fun transImagePager(contents: List<Attachment>)
    }
}