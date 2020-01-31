package com.nanoyatsu.nastodon.view.imageViewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.databinding.FragmentImageViewerBinding

class ImageViewerFragment : Fragment() {
    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            url = it.getString(ARG_KEY_URL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentImageViewerBinding.inflate(inflater, container, false)
            .also { initBinding(it) }
        return binding.root
    }

    fun initBinding(binding: FragmentImageViewerBinding) {
        Glide.with(binding.image)
            .load(url)
            .apply {
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            }
            .into(binding.image)
    }

    companion object {
        const val ARG_KEY_URL = "URL"
        fun newInstance(url: String) =
            ImageViewerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_KEY_URL, url)
                }
            }
    }
}
