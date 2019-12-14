package com.nanoyatsu.nastodon.view.timelineFrame.timeline

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.entity.Status
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
                .also {
                    val factory =
                        TimelineViewModelFactory(TimelineViewModel.GetMethod.HOME, auth, apiManager)
                    it.vm = ViewModelProvider(this, factory).get(TimelineViewModel::class.java)
                    it.lifecycleOwner = this
                }

        return inflater.inflate(R.layout.content_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // SwipeRefreshLayout 引っ張って更新するやつ
        binding.swipeRefresh.setOnRefreshListener {
            CoroutineScope(context = Dispatchers.Main).launch {
                initTimeline()
                binding.swipeRefresh.isRefreshing = false
            }
        }

        eventListener?.progressStart()
        CoroutineScope(context = Dispatchers.Main).launch {
            initTimeline()
            eventListener?.progressEnd()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private suspend fun initTimeline() {
        val context = this.context ?: return

        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.timelineView.layoutManager = layoutManager // fixme 画面回転を連続したりするとNPE
        val timeline = mutableListOf<Status>() // todo ViewModelに持つ

        val adapter = TimelineAdapter(context)
        binding.timelineView.adapter = adapter
        adapter.submitList(timeline)

        reloadTimeline(timeline)
    }


    private suspend fun reloadTimeline(
        timeline: List<Status>, maxId: String? = null, sinceId: String? = null
    ) {
        val apiDir = apiManager.timelines
        val token = auth.accessToken
        val getter = suspend { binding.vm!!.getMethod.getter(apiDir, token, maxId, sinceId) }
        val toots = binding.vm!!.getByApi(getter)
        // 仮記述 今のままだと1回しか増えない(timelineを更新していない) todo
        val adapter = binding.timelineView.adapter
        if (adapter is TimelineAdapter)
            adapter.submitList(timeline + toots.toList())
    }

    fun focusTop() {
        binding.timelineView.smoothScrollToPosition(0)
    }

    interface EventListener {
        fun progressStart()
        fun progressEnd()
    }
}