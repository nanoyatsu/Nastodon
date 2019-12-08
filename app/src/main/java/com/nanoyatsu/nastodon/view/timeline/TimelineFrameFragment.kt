package com.nanoyatsu.nastodon.view.timeline


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.databinding.FragmentTimelineFrameBinding

class TimelineFrameFragment : Fragment(), TimelineFragment.EventListener {

    lateinit var binding: FragmentTimelineFrameBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<FragmentTimelineFrameBinding>(
            activity!!, R.layout.fragment_timeline_frame
        ).also {
            it.vm = ViewModelProvider(this@TimelineFrameFragment).get(MainViewModel::class.java)
            it.lifecycleOwner = this@TimelineFrameFragment
        }

        // FloatingButton todo 関数化・処理分割
        binding.floatingEdit.setOnClickListener {
            findNavController().navigate(TimelineFrameFragmentDirections.actionTimelineFrameFragmentToTootEditFragment())
        }

        // 下部タブ
        setTabButton(activity!!.supportFragmentManager)

        // 初期化あるいは再構成
        restoreView(binding)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline_frame, container, false)
    }

    private fun restoreView(binding: FragmentTimelineFrameBinding) {
        binding.navigation.findViewById<View>(binding.vm!!.selectedTabId).callOnClick()
    }

    private val timelineTabs =
        arrayOf(R.id.navigation_timeline, R.id.navigation_notice, R.id.navigation_global_timeline)
            .zip(TimelineFragment.GetMethod.values())

    private fun setTabButton(fm: FragmentManager) {
        fun fragmentTransition(
            selected: Pair<Int, TimelineFragment.GetMethod>,
            showing: TimelineFragment?
        ) {
            binding.vm?.selectedTabId = selected.first
            fm.beginTransaction().also { trans ->
                fm.fragments.forEach { trans.hide(it) }

                if (showing == null)
                    trans.add(
                        R.id.content_main,
                        TimelineFragment.newInstance(selected.second),
                        selected.second.name
                    )
                else
                    trans.show(showing)
                trans.commit()
            }
        }

        val selectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            val selected = timelineTabs.find { it.first == item.itemId }
                ?: return@OnNavigationItemSelectedListener false
            val showing = fm.findFragmentByTag(selected.second.name) as? TimelineFragment

            if (showing != null && binding.vm?.selectedTabId == selected.first)
                showing.focusTop()
            else
                fragmentTransition(selected, showing)
            true
        }

        binding.navigation.setOnNavigationItemSelectedListener(selectedListener)
    }

    override fun progressStart() {
        binding.vm?.progressStart()
    }

    override fun progressEnd() {
        binding.vm?.progressEnd()
    }
}
