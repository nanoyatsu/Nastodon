package com.nanoyatsu.nastodon.view.mainBottomNavigation.timeline


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.nanoyatsu.nastodon.components.ZoomOutPageTransformer
import com.nanoyatsu.nastodon.databinding.FragmentTimelineFrameBinding

/**
 * MainBottomNavigationFragment -> this -> TimelineFragment
 */
class TimelineFrameFragment : Fragment() {

    lateinit var binding: FragmentTimelineFrameBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimelineFrameBinding.inflate(inflater, container, false)
            .also { initBinding(it) }
        return binding.root
    }

    private fun initBinding(binding: FragmentTimelineFrameBinding) {
        val pagerAdapter = TimelinePagerAdapter(activity!!)
        binding.pager.adapter = pagerAdapter
        binding.pager.setPageTransformer(ZoomOutPageTransformer())

        TabLayoutMediator(binding.pagerTab, binding.pager) { tab, pos ->
            tab.text = TimelineViewModel.Kind.values()[pos].name
        }
            .attach()
    }

    private inner class TimelinePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = TimelineViewModel.Kind.values().size

        override fun createFragment(position: Int): Fragment {
            val kind = TimelineViewModel.Kind.values()[position]
            return TimelineFragment.newInstance(kind)
        }
    }
}