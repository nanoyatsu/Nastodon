package com.nanoyatsu.nastodon.view.timelineFrame.timeline


import androidx.paging.ItemKeyedDataSource
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiTimelines
import com.nanoyatsu.nastodon.data.api.entity.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class TimelineDataSource(
    private val timelineKind: TimelineViewModel.Kind,
    private val apiDir: MastodonApiTimelines,
    private val token: String
) : ItemKeyedDataSource<String, Status>() {

    // todo エラー処理 https://github.com/android/architecture-components-samples/tree/master/PagingWithNetworkSample も参考になる
//    private suspend fun getByApi(getter: suspend () -> Response<List<Status>>): List<Status> {
//        return try {
//            val res = getter()
//            res.body() ?: listOf() // todo レスポンスが期待通りじゃないときの処理 res.errorBody()
//        } catch (e: HttpException) {
//            e.printStackTrace()
//            // todo 通信失敗のときの処理
//            listOf()
//        }
//    }

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