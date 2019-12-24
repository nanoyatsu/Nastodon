package com.nanoyatsu.nastodon.view.mainBottomNavigation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.view.mainBottomNavigation.BottomNavigatedFragmentInterface

class SearchFragment : Fragment(), BottomNavigatedFragmentInterface {

    companion object {
        fun newInstance() = SearchFragment()
    }

    private lateinit var viewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fagment_search, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun toTimeline() =
        findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToTimelineFrameFragment())

    override fun toNotice() =
        findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToNoticeFrameFragment())

    override fun toSearch() {}

}
