package com.nanoyatsu.nastodon.view.timeline

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.entity.Attachment
import com.nanoyatsu.nastodon.data.api.entity.Status
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.databinding.ItemTootBinding
import com.nanoyatsu.nastodon.view.tootDetail.MediaAttachmentAdapter
import com.nanoyatsu.nastodon.view.tootDetail.TootViewModel
import kotlinx.android.synthetic.main.activity_nav_host.*

class TimelineItemViewHolder(val binding: ItemTootBinding) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): TimelineItemViewHolder {
            val binding =
                ItemTootBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return TimelineItemViewHolder(
                binding
            )
        }
    }

    fun bind(context: Context, toot: Status, apiManager: MastodonApiManager, auth: AuthInfo) {
        require(context is FragmentActivity)
        setupAttachments(context, binding.attachments, toot.mediaAttachments)

        val vm = TootViewModel(
            toot,
            auth,
            apiManager
        )
        vm.timeClickEvent.observe(context, Observer { if (it) transTootDetail(context, vm) })
        vm.replyEvent.observe(context, Observer { if (it) transTootEditAsReply(context, vm) })
        vm.reblogEvent.observe(context, Observer { if (it) vm.doReblog() })
        vm.favouriteEvent.observe(context, Observer { if (it) vm.doFav() })
        binding.lifecycleOwner = context

        binding.vm = vm

        binding.executePendingBindings()
    }

    private fun setupAttachments(
        context: Context, view: RecyclerView, contents: List<Attachment>
    ) {
        val layoutManager =
            GridLayoutManager(context, 2)
        view.layoutManager = layoutManager
        view.adapter = MediaAttachmentAdapter(
            contents.toTypedArray()
        ).apply {
            publicListener = object :
                MediaAttachmentAdapter.ThumbnailClickListener {
                override val onThumbnailClick = { transImagePager(context, contents) }
            }
        }
    }

    // todo interface化、親Fragmentで処理を持つ
    private fun navigate(context: Context, directions: NavDirections) {
        if (context is FragmentActivity)
            context.main_fragment_container.findNavController().navigate(directions)
    }

    private fun transTootEditAsReply(context: Context, vm: TootViewModel) {
        val directions =
            TimelineFrameFragmentDirections.actionTimelineFrameFragmentToTootEditFragment(
                vm.toot.value
            )
        navigate(context, directions)
        vm.onReplyClickFinished()
    }

    private fun transTootDetail(context: Context, vm: TootViewModel) {
        val directions =
            TimelineFrameFragmentDirections.actionTimelineFrameFragmentToTootDetailFragment(
                vm.toot.value!!
            )
        navigate(context, directions)
        vm.onTimeClickFinished()
    }

    fun transImagePager(context: Context, contents: List<Attachment>) {
        val directions =
            TimelineFrameFragmentDirections.actionTimelineFrameFragmentToImagePagerFragment(
                contents.map { it.url }.toTypedArray()
            )
        navigate(context, directions)
    }
}