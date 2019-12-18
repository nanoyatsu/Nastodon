package com.nanoyatsu.nastodon.view.mainBottomNavigation.timeline


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nanoyatsu.nastodon.R
import kotlinx.android.synthetic.main.fragment_timeline_frame.*

/**
 * MainBottomNavigationFragment -> this -> TimelineFragment
 */
class TimelineFrameFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timeline_frame, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pagerAdapter = TimelinePagerAdapter(activity!!)
        pager.adapter = pagerAdapter

    }

    private inner class TimelinePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = TimelineViewModel.Kind.values().size

        override fun createFragment(position: Int): Fragment {
            val kind = TimelineViewModel.Kind.values()[position]
            return TimelineFragment() // todo newInstance(kind)
        }
    }
}
