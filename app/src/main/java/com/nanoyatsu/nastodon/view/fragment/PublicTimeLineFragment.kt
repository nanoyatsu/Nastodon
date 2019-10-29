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
import com.nanoyatsu.nastodon.view.MainActivity
import com.nanoyatsu.nastodon.view.adapter.TimelineAdapter
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.HttpException

class PublicTimeLineFragment() : Fragment() {

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

        context.progressStart()
        CoroutineScope(context = Dispatchers.Main).launch {
            reloadPublicTimeline(pref.accessToken, pref.instanceUrl)
            context.progressEnd()
        }
    }

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
}