package com.nanoyatsu.nastodon.components.networkState

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.databinding.ItemNetworkStateBinding

class NetworkStateItemViewHolder(
    private val binding: ItemNetworkStateBinding,
    private val retryCallback: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    init {
//        retry.setOnClickListener { retryCallback() }
    }

    fun bind(networkState: NetworkState?) {
        binding.textView.text = "${networkState?.msg}"
    }

    companion object {
        fun from(parent: ViewGroup, retryCallback: () -> Unit): NetworkStateItemViewHolder {
            val binding =
                ItemNetworkStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return NetworkStateItemViewHolder(binding, retryCallback)
        }

        fun toVisibility(constraint: Boolean): Int {
            return if (constraint) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}
