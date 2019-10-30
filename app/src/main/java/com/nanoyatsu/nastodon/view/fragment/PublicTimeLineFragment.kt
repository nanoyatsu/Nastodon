package com.nanoyatsu.nastodon.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.model.AuthPreferenceManager
import com.nanoyatsu.nastodon.model.Status
import com.nanoyatsu.nastodon.presenter.MastodonApiManager
import com.nanoyatsu.nastodon.presenter.MastodonApiTimelines
import com.nanoyatsu.nastodon.view.MainActivity
import com.nanoyatsu.nastodon.view.adapter.TimelineAdapter
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.HttpException

class PublicTimeLineFragment() : Fragment() {

    private lateinit var getMethod: GetMethod

    // todo 外から入れるようにする
    private lateinit var timelinesApi: MastodonApiTimelines
    private lateinit var pref: AuthPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            getMethod =
                GetMethod.values().find { it.name == bundle.getString(BundleKey.GET_METHOD.name) } ?: GetMethod.HOME
        }

        val context = this.context as? MainActivity ?: return
        pref = AuthPreferenceManager(context)
        if (pref.instanceUrl == "") return // そのまま認証に行ってもいい
        timelinesApi = MastodonApiManager(pref.instanceUrl).timelines
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.content_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loading()
    }

    private fun loading() {
        val context = this.context as? MainActivity ?: return
        val pref = AuthPreferenceManager(context)
        if (pref.instanceUrl == "")
            return

        context.progressStart() // review リスナー化したほうがよいか？
        CoroutineScope(context = Dispatchers.Main).launch {
            reloadPublicTimeline(pref.accessToken, pref.instanceUrl)
            context.progressEnd()
        }
    }

    // todo 同じ型シグネチャで取得関数を用意する enumに紐付けたいが、staticとの絡みで案が必要
    private suspend fun callHomeTimeline() =
        timelinesApi.getHomeTimeline(authorization = pref.accessToken)

    private suspend fun callLocalPublicTimeline() =
        timelinesApi.getPublicTimeline(authorization = pref.accessToken, local = true)

    private suspend fun callGlobalPublicTimeline() =
        timelinesApi.getPublicTimeline(authorization = pref.accessToken, local = false)

    private suspend fun reloadPublicTimeline(token: String, url: String) {
        val context = this.context ?: return
        val api = MastodonApiManager(url).timelines
        val response = CoroutineScope(context = Dispatchers.IO).async {
            try {
                val res = api.getPublicTimeline(authorization = token, local = true)
                res.body()
                // todo レスポンスが期待通りじゃないとき
            } catch (e: HttpException) {
                e.printStackTrace()
                null
            }
        }
        val toots = response.await()
        if (toots is Array<Status>) {
            val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            timelineView.layoutManager = layoutManager

            val toArrayList = arrayListOf<Status>().also { it.addAll(toots) }
            val adapter = TimelineAdapter(context, toArrayList)
            timelineView.adapter = adapter
            adapter.notifyDataSetChanged()
        } else {
            // ここだと res.errorBody() できないのでまた考える
        }
    }

    override fun onResume() {
        super.onResume()
        loading()  // 仮置
    }

    companion object {
        enum class BundleKey { GET_METHOD }
        enum class GetMethod { HOME, LOCAL, GLOBAL, SEARCH }

        @JvmStatic
        fun newInstance(method: GetMethod) =
            HomeTimeLineFragment().apply {
                arguments = Bundle().apply {
                    putString(BundleKey.GET_METHOD.name, method.name)
                }
            }
    }

}