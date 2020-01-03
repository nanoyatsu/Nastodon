package com.nanoyatsu.nastodon.view.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.nanoyatsu.nastodon.components.ZoomOutPageTransformer
import com.nanoyatsu.nastodon.databinding.FragmentNoticeFrameBinding

class NoticeFrameFragment : Fragment() {

    lateinit var binding: FragmentNoticeFrameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoticeFrameBinding.inflate(inflater, container, false)
            .also { initBinding(it) }
        return binding.root
    }

    private fun initBinding(binding: FragmentNoticeFrameBinding) {
        val pagerAdapter = NoticePagerAdapter(activity!!)
        binding.pager.adapter = pagerAdapter
        binding.pager.setPageTransformer(ZoomOutPageTransformer())

        TabLayoutMediator(binding.pagerTab, binding.pager)
        { tab, pos -> tab.text = NoticeViewModel.Kind.values()[pos].name }
            .attach()
    }

    private inner class NoticePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NoticeViewModel.Kind.values().size

        override fun createFragment(position: Int): Fragment {
            val kind = NoticeViewModel.Kind.values()[position]
            return NoticeFragment.newInstance(kind)
        }
    }
}
