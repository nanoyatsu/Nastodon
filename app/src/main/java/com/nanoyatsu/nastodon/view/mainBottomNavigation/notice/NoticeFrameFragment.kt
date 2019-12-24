package com.nanoyatsu.nastodon.view.mainBottomNavigation.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.view.mainBottomNavigation.BottomNavigatedFragmentInterface

class NoticeFrameFragment : Fragment(), BottomNavigatedFragmentInterface {

    companion object {
        fun newInstance() = NoticeFrameFragment()
    }

    private lateinit var viewModel: NoticeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notice_frame, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(NoticeViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun toTimeline() =
        findNavController().navigate(NoticeFrameFragmentDirections.actionNoticeFrameFragmentToTimelineFrameFragment())

    override fun toNotice() {}

    override fun toSearch() =
        findNavController().navigate(NoticeFrameFragmentDirections.actionNoticeFrameFragmentToSearchFragment())
}
