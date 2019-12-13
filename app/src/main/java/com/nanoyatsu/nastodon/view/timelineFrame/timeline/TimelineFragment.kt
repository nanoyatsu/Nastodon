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
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiTimelines
import com.nanoyatsu.nastodon.data.api.entity.Status
import com.nanoyatsu.nastodon.data.database.NastodonDataBase
import com.nanoyatsu.nastodon.data.database.dao.AuthInfoDao
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.databinding.ContentMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import retrofit2.Response

class TimelineFragment() : Fragment() {

    private var eventListener: EventListener? = null
    private lateinit var binding: ContentMainBinding

    // todo 外から入れるようにする
    private lateinit var timelinesApi: MastodonApiTimelines
    private lateinit var authInfoDao: AuthInfoDao
    private lateinit var auth: AuthInfo
    private lateinit var apiManager: MastodonApiManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventListener = context as? EventListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authInfoDao = NastodonDataBase.getInstance().authInfoDao()
        // todo マルチアカウント考慮
        runBlocking(context = Dispatchers.IO) { auth = authInfoDao.getAll().first() }
        if (auth.instanceUrl == "") return // todo 認証に行く
        apiManager = MastodonApiManager(auth.instanceUrl)

        timelinesApi = apiManager.timelines
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding =
            DataBindingUtil.setContentView<ContentMainBinding>(activity!!, R.layout.content_main)
                .also {
                    val factory = TimelineViewModelFactory(TimelineViewModel.GetMethod.HOME)
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

    // todo 同じ型シグネチャで取得関数を用意する enumに紐付けたいが、staticとの絡みで案が必要
    private suspend fun callHomeTimeline(maxId: String?, sinceId: String?) =
        timelinesApi.getHomeTimeline(auth.accessToken, maxId, sinceId)

    private suspend fun callLocalPublicTimeline(maxId: String?, sinceId: String?) =
        timelinesApi.getPublicTimeline(
            authorization = auth.accessToken, local = true, maxId = maxId, sinceId = sinceId
        )

    private suspend fun callGlobalPublicTimeline(maxId: String?, sinceId: String?) =
        timelinesApi.getPublicTimeline(
            authorization = auth.accessToken, local = false, maxId = maxId, sinceId = sinceId
        )

    private fun returnTimelineGetter(getMethod: TimelineViewModel.GetMethod): suspend ((String?, String?) -> Response<Array<Status>>) {
        val callApi = when (getMethod) {
            TimelineViewModel.GetMethod.HOME -> ::callHomeTimeline
            TimelineViewModel.GetMethod.LOCAL -> ::callLocalPublicTimeline
            TimelineViewModel.GetMethod.GLOBAL -> ::callGlobalPublicTimeline
        }
        return { maxId: String?, sinceId: String? -> callApi(maxId, sinceId) }
    }

    private suspend fun reloadTimeline(
        timeline: List<Status>, maxId: String? = null, sinceId: String? = null
    ) {
        val getter = suspend { returnTimelineGetter(binding.vm!!.getMethod)(maxId, sinceId) }
        val toots = getByApi(getter)
        // 仮記述 今のままだと1回しか増えない(timelineを更新していない) todo
        val adapter = binding.timelineView.adapter
        if (adapter is TimelineAdapter)
            adapter.submitList(timeline + toots.toList())
    }

    private suspend fun getByApi(getter: suspend () -> Response<Array<Status>>): Array<Status> {
        return try {
            val res = getter()
            res.body() ?: arrayOf() // todo レスポンスが期待通りじゃないときの処理 res.errorBody()
        } catch (e: HttpException) {
            e.printStackTrace()
            // todo 通信失敗のときの処理
            arrayOf()
        }
    }

    fun focusTop() {
        binding.timelineView.smoothScrollToPosition(0)
    }

    interface EventListener {
        fun progressStart()
        fun progressEnd()
    }
}