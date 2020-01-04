package com.nanoyatsu.nastodon.view.timeline

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.databinding.FragmentTimelineBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class TimelineFragment : Fragment() {

    private var eventListener: EventListener? = null
    private lateinit var binding: FragmentTimelineBinding
    lateinit var kind: TimelineViewModel.Kind

    @Inject
    lateinit var auth: AuthInfo
    @Inject
    lateinit var apiManager: MastodonApiManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity!!.application as NastodonApplication).appComponent.inject(this)
        eventListener = context as? EventListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (auth.instanceUrl == "") return // todo 認証に行く
        arguments?.let {
            kind = TimelineViewModel.Kind.values().getOrNull(it.getInt(ARG_KEY_KIND))
                ?: TimelineViewModel.Kind.HOME
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimelineBinding.inflate(inflater, container, false)
            .also { initBinding(it) }
        return binding.root
    }

    private fun initBinding(binding: FragmentTimelineBinding) {
        val factory = TimelineViewModelFactory(kind, auth, apiManager)
        binding.vm = ViewModelProvider(this, factory).get(TimelineViewModel::class.java)
        binding.lifecycleOwner = this

        val context = this.context ?: return
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.timelineView.layoutManager = layoutManager // fixme 画面回転を連続したりするとNPE
        val adapter = TimelineAdapter(context)
        binding.timelineView.adapter = adapter

        // Timelineの常時更新
        binding.vm!!.timeline.observe(viewLifecycleOwner, Observer { adapter.submitList(it) })

        // SwipeRefreshLayout 引っ張って初期化する部品
        binding.swipeRefresh.setOnRefreshListener {
            initTimeline()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        eventListener?.progressStart()
        CoroutineScope(context = Dispatchers.Main).launch {
            // fixme paging化の関係で特に意味がないスコープ
            initTimeline()
            eventListener?.progressEnd()
        }
    }

    private fun initTimeline() {
        binding.vm!!.clearTimeline()
    }


    fun focusTop() {
        binding.timelineView.smoothScrollToPosition(0)
    }

    interface EventListener {
        fun progressStart()
        fun progressEnd()
    }

    companion object {
        const val ARG_KEY_KIND = "KIND"
        fun newInstance(kind: TimelineViewModel.Kind) =
            TimelineFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_KEY_KIND, kind.ordinal)
                }
            }
    }
}