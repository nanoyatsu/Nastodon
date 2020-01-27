package com.nanoyatsu.nastodon.view.notice

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.data.domain.Notification
import com.nanoyatsu.nastodon.data.domain.Status
import com.nanoyatsu.nastodon.databinding.ItemNoticeBinding

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
        require(context is FragmentActivity)
        val vm = NoticeItemViewModel(notice)

        vm.contentClickEvent.observe(context, Observer { if (it) onContentClick(vm) })
        binding.lifecycleOwner = context

        // todo XML側で解決する（必要ならBindingAdapter）
        binding.description.text =
            context.getString(vm.notice.type.descriptionId, vm.notice.account.displayName)
        val icon = vm.notice.type.icon
        binding.typeIcon.background =
            context.getDrawable(icon.iconId)?.apply { setTint(context.getColor(icon.colorId)) }

        binding.vm = vm
    }

    private fun onContentClick(vm: NoticeItemViewModel) {
        vm.notice.status?.let { navigation?.transTootDetail(vm.notice.status) }
        vm.onContentClickFinished()
    }

    interface Navigation {
        fun transTootDetail(toot: Status)
    }
}
