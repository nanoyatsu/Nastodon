package com.nanoyatsu.nastodon.view.timelineFrame.timeline


import androidx.paging.ItemKeyedDataSource
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiTimelines
import com.nanoyatsu.nastodon.data.api.entity.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class TimelineDataSource(
    private val apiDir: MastodonApiTimelines,
    private val token: String,
    private val timelineKind: TimelineViewModel.Kind
) : ItemKeyedDataSource<String, Status>() {

    fun fetchData() {

    }

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<Status>
    ) {
        val response = runBlocking(Dispatchers.IO)
        { timelineKind.getter(apiDir, token, null, null) }

        val statuses = response.body() ?: emptyList()
        callback.onResult(statuses)
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<Status>) {
        val response = runBlocking(Dispatchers.IO)
        { timelineKind.getter(apiDir, token, params.key, null) }

        val statuses = response.body() ?: emptyList()
        callback.onResult(statuses)

    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<Status>) {
        // なにもしない 未来方向のloadは実装しない
    }

    override fun getKey(item: Status): String = item.id
}