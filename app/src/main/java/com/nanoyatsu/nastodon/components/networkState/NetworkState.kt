package com.nanoyatsu.nastodon.components.networkState

enum class NetworkStatus { SUCCESS, RUNNING, FAILED }

// 可変のメッセージを持たせるため、enum(NetworkStatus)をラップする構成
@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(val status: NetworkStatus, val msg: String? = null) {
    companion object {
        val LOADED = NetworkState(NetworkStatus.SUCCESS)
        val LOADING = NetworkState(NetworkStatus.RUNNING)

        fun error(msg: String?) = NetworkState(NetworkStatus.FAILED, msg)
    }
}