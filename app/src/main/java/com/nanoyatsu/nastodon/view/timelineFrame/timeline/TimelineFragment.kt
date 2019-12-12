package com.nanoyatsu.nastodon.view.timelineFrame.timeline

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiTimelines
import com.nanoyatsu.nastodon.data.api.entity.Status
import com.nanoyatsu.nastodon.data.database.NastodonDataBase
import com.nanoyatsu.nastodon.data.database.dao.AuthInfoDao
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import retrofit2.Response

class TimelineFragment() : Fragment() {
    enum class GetMethod { HOME, LOCAL, GLOBAL }

    private var eventListener: EventListener? = null
    private lateinit var getMethod: GetMethod

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
        getMethod = GetMethod.HOME // todo viewModelで持つ 初期値は設定可能にしたい

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
        return inflater.inflate(R.layout.content_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // SwipeRefreshLayout 引っ張って更新するやつ
        swipe_refresh.setOnRefreshListener {
            CoroutineScope(context = Dispatchers.Main).launch {
                initTimeline()
                swipe_refresh.isRefreshing = false
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
        timelineView.layoutManager = layoutManager // fixme 画面回転を連続したりするとNPE
        val timeline = mutableListOf<Status>() // todo ViewModelに持つ

        val adapter = TimelineAdapter(context)
        timelineView.adapter = adapter
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

    private fun returnTimelineGetter(getMethod: GetMethod): suspend ((String?, String?) -> Response<Array<Status>>) {
        val callApi = when (getMethod) {
            GetMethod.HOME -> ::callHomeTimeline
            GetMethod.LOCAL -> ::callLocalPublicTimeline
            GetMethod.GLOBAL -> ::callGlobalPublicTimeline
        }
        return { maxId: String?, sinceId: String? -> callApi(maxId, sinceId) }
    }

    private suspend fun reloadTimeline(
        timeline: List<Status>, maxId: String? = null, sinceId: String? = null
    ) {
        val getter = suspend { returnTimelineGetter(getMethod)(maxId, sinceId) }
        val toots = getByApi(getter)
        // 仮記述 今のままだと1回しか増えない(timelineを更新していない) todo
        val adapter = timelineView.adapter
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
        timelineView.smoothScrollToPosition(0)
    }

    interface EventListener {
        fun progressStart()
        fun progressEnd()
    }
}