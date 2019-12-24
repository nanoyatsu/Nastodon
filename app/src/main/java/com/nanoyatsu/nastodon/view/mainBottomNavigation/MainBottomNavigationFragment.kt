package com.nanoyatsu.nastodon.view.mainBottomNavigation


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.databinding.FragmentMainBottomNavigationBinding
import com.nanoyatsu.nastodon.view.mainBottomNavigation.timeline.TimelineFragment

class MainBottomNavigationFragment : Fragment(), TimelineFragment.EventListener {

    lateinit var binding: FragmentMainBottomNavigationBinding

    enum class Tab(val id: Int) {
        TIMELINE(R.id.frame_tab_timeline),
        NOTICE(R.id.frame_tab_notice),
        SEARCH(R.id.frame_tab_search)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBottomNavigationBinding.inflate(inflater, container, false)
            .also { initBinding(it) }
        return binding.root
    }

    private fun initBinding(binding: FragmentMainBottomNavigationBinding) {
        binding.vm = ViewModelProvider(this)
            .get(MainBottomNavigationViewModel::class.java)
        binding.lifecycleOwner = this

        // FloatingButton
        binding.vm!!.tootEvent.observe(viewLifecycleOwner, Observer { if (it) transTootEdit() })

        // 下部タブ
        binding.navigation.setOnNavigationItemSelectedListener { binding.vm!!.onSelectedMenuItem(it) }
        binding.vm!!.selectedTabId.observe(viewLifecycleOwner, Observer { onChangeTabId(it) })
    }

    private fun onChangeTabId(id: Int) {
        val selected = Tab.values().find { it.id == id }
            ?: return

        // todo navigationで表示中のFragmentの取得
        val showing: Fragment? = null

        if (showing is BottomNavigatedFragmentInterface)
            when (selected) {
                Tab.TIMELINE -> showing.toTimeline()
                Tab.NOTICE -> showing.toNotice()
                Tab.SEARCH -> showing.toSearch()
            }
    }

    private fun transTootEdit() {
        findNavController().navigate(MainBottomNavigationFragmentDirections.actionMainBottomNavigationFragmentToTootEditFragment())
        binding.vm!!.onTootClickFinished()
    }

    override fun progressStart() {
        binding.vm?.progressStart()
    }

    override fun progressEnd() {
        binding.vm?.progressEnd()
    }
}
