package com.nanoyatsu.nastodon.components.networkState

import androidx.lifecycle.LiveData
import androidx.paging.PagedList

// https://github.com/android/architecture-components-samples/tree/master/PagingWithNetworkSample
// を参考 androidx.paging.xxDataSource から通信状態系の情報も受け取る時にまとめるためのデータクラス
// 自分の実装では一旦使わないかも（ViewModel内でDataSource生成をしてしまうため）

data class Listing<T>(
    // the LiveData of paged lists for the UI to observe
    // UIが監視するページングされたリストのLiveData
    val pagedList: LiveData<PagedList<T>>,
    // represents the network request status to show to the user
    // ユーザーに表示するネットワーク要求ステータスを表します
    val networkState: LiveData<NetworkState>,
    // represents the refresh status to show to the user. Separate from networkState, this
    // value is importantly only when refresh is requested.
    // ユーザーに表示する更新ステータスを表します。 networkStateとは別に、値は更新が要求された場合にのみ重要です。
    val isRefreshing: LiveData<Boolean>,
    // refreshes the whole data and fetches it from scratch.
    // データ全体を更新し、ゼロからフェッチします。
    val refresh: () -> Unit,
    // retries any failed requests.
    // 失敗したリクエストを再試行します。
    val retry: () -> Unit
)

