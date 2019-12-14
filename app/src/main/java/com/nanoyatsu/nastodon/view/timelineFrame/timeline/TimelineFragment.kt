package com.nanoyatsu.nastodon.view.timelineFrame.timeline

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.databinding.ContentMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class TimelineFragment() : Fragment() {

    private var eventListener: EventListener? = null
    private lateinit var binding: ContentMainBinding

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding =
            DataBindingUtil.setContentView<ContentMainBinding>(activity!!, R.layout.content_main)
                .also { initBinding(it) }

        return inflater.inflate(R.layout.content_main, container, false)
    }

    private fun initBinding(binding: ContentMainBinding) {
        val factory =
            TimelineViewModelFactory(TimelineViewModel.Kind.HOME, auth, apiManager)
        binding.vm = ViewModelProvider(this, factory).get(TimelineViewModel::class.java)
        binding.lifecycleOwner = this

        val context = this.context ?: return
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.timelineView.layoutManager = layoutManager // fixme 画面回転を連続したりするとNPE
        val adapter = TimelineAdapter(context)
        binding.timelineView.adapter = adapter

        // Timelineの常時更新
        binding.vm!!.timeline.observe(viewLifecycleOwner, Observer {
            (binding.timelineView.adapter as? TimelineAdapter)?.submitList(it)
        })

        // SwipeRefreshLayout 引っ張って初期化する部品
        binding.swipeRefresh.setOnRefreshListener {
            CoroutineScope(context = Dispatchers.Main).launch {
                initTimeline()
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        eventListener?.progressStart()
        CoroutineScope(context = Dispatchers.Main).launch {
            initTimeline()
            eventListener?.progressEnd()
        }
    }

    private suspend fun initTimeline() {
        binding.vm!!.clearTimeline()
        binding.vm!!.reloadTimeline()
    }


    fun focusTop() {
        binding.timelineView.smoothScrollToPosition(0)
    }

    interface EventListener {
        fun progressStart()
        fun progressEnd()
    }
}