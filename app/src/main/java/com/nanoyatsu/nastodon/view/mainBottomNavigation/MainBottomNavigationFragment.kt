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
import com.nanoyatsu.nastodon.view.mainBottomNavigation.notice.NoticeFragment
import com.nanoyatsu.nastodon.view.mainBottomNavigation.search.SearchFragment
import com.nanoyatsu.nastodon.view.mainBottomNavigation.timeline.TimelineFragment
import com.nanoyatsu.nastodon.view.mainBottomNavigation.timeline.TimelineFrameFragment

class MainBottomNavigationFragment : Fragment(), TimelineFragment.EventListener {

    lateinit var binding: FragmentMainBottomNavigationBinding

    enum class Tab(val id: Int, val fragmentClass: Class<out Fragment>) {
        TIMELINE(R.id.frame_tab_timeline, TimelineFrameFragment::class.java),
        NOTICE(R.id.frame_tab_notice, NoticeFragment::class.java),
        SEARCH(R.id.frame_tab_search, SearchFragment::class.java)
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
//        binding = DataBindingUtil.setContentView<FragmentMainBottomNavigationBinding>(
//            activity!!, R.layout.fragment_main_bottom_navigation
//        ).also {
//            it.vm =
//                ViewModelProvider(this@MainBottomNavigationFragment).get(
//                    MainBottomNavigationViewModel::class.java
//                )
//            it.lifecycleOwner = this@MainBottomNavigationFragment
//        }
//
//        // FloatingButton
//        binding.vm!!.tootEvent.observe(this, Observer { if (it) transTootEdit() })
//
//        // 下部タブ
//        binding.navigation.setOnNavigationItemSelectedListener { binding.vm!!.onSelectedMenuItem(it) }
//        binding.vm!!.selectedTabId.observe(this, Observer { onChangeTabId(it) })
//        return inflater.inflate(R.layout.fragment_main_bottom_navigation, container, false)

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
        val fm = activity!!.supportFragmentManager
        fun fragmentTransition(selected: Tab, showing: Fragment?) {
            val trans = fm.beginTransaction()
            // 一旦全部隠す
            fm.fragments.forEach { trans.hide(it) }

            if (showing == null) {
                val fragment = selected.fragmentClass.newInstance()
                trans.add(R.id.timeline_container, fragment, selected.fragmentClass.simpleName)
            } else
                trans.show(showing)
            trans.commit()
        }

        // showingがnull : 初めて選ばれた
        // showingがnot-null OR 非表示：そこだけshow()する
        // showingがnot-null OR 表示：Timelineの先頭に飛ぶ
        val selected = Tab.values().find { it.id == id }
            ?: return
        val showing = fm.findFragmentByTag(selected.fragmentClass.simpleName)

        if (showing == null || showing.isHidden)
            fragmentTransition(selected, showing)
        else
            (showing as? TimelineFragment)?.focusTop()
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
