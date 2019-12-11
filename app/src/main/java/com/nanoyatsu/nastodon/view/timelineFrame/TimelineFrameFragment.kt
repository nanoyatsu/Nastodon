package com.nanoyatsu.nastodon.view.timelineFrame


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.databinding.FragmentTimelineFrameBinding

class TimelineFrameFragment : Fragment(), TimelineFragment.EventListener {

    lateinit var binding: FragmentTimelineFrameBinding

    companion object {
        // review enumに組み込むべきか
        private val timelineTabs =
            arrayOf(
                R.id.frame_tab_timeline, R.id.frame_tab_notice, R.id.frame_tab_search
            ).zip(TimelineFragment.GetMethod.values())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<FragmentTimelineFrameBinding>(
            activity!!, R.layout.fragment_timeline_frame
        ).also {
            it.vm =
                ViewModelProvider(this@TimelineFrameFragment).get(TimelineFrameViewModel::class.java)
            it.lifecycleOwner = this@TimelineFrameFragment
        }

        // FloatingButton
        binding.vm!!.tootEvent.observe(this, Observer { if (it) transTootEdit() })

        // 下部タブ
        binding.navigation.setOnNavigationItemSelectedListener { binding.vm!!.onSelectedMenuItem(it) }
        binding.vm!!.selectedTabId.observe(this, Observer { onChangeTabId(it) })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timeline_frame, container, false)
    }

    private fun onChangeTabId(id: Int) {
        val fm = activity!!.supportFragmentManager
        fun fragmentTransition(
            selected: Pair<Int, TimelineFragment.GetMethod>, showing: TimelineFragment?
        ) {
            val trans = fm.beginTransaction()
            // 一旦全部隠す
            fm.fragments.forEach { trans.hide(it) }

            if (showing == null) {
                val fragment = TimelineFragment.newInstance(selected.second)
                trans.add(R.id.content_main, fragment, selected.second.name)
            } else
                trans.show(showing)
            trans.commit()
        }

        // showingがnull : 初めて選ばれた
        // showingがnot-null OR 非表示：そこだけshow()する
        // showingがnot-null OR 表示：Timelineの先頭に飛ぶ（LiveDataにした関係で）
        val selected = timelineTabs.find { it.first == id }
            ?: return
        val showing = fm.findFragmentByTag(selected.second.name) as? TimelineFragment

        if (showing == null || showing.isHidden)
            fragmentTransition(selected, showing)
        else
            showing.focusTop()
    }

    private fun transTootEdit() {
        findNavController().navigate(TimelineFrameFragmentDirections.actionTimelineFrameFragmentToTootEditFragment())
        binding.vm!!.onTootClickFinished()
    }

    override fun progressStart() {
        binding.vm?.progressStart()
    }

    override fun progressEnd() {
        binding.vm?.progressEnd()
    }
}
