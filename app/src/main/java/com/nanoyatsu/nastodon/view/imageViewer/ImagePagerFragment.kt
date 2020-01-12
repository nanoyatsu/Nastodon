package com.nanoyatsu.nastodon.view.imageViewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nanoyatsu.nastodon.databinding.FragmentImagePagerBinding

class ImagePagerFragment : Fragment() {

    lateinit var binding: FragmentImagePagerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImagePagerBinding.inflate(inflater, container, false)
            .also { initBinding(it) }
        return binding.root
    }

    private fun initBinding(binding: FragmentImagePagerBinding) {
        val args = ImagePagerFragmentArgs.fromBundle(arguments!!)
        binding.pager.adapter = ImagePagerAdapter(args.urls, activity!!)
    }

    private inner class ImagePagerAdapter(val urls: Array<String>, fa: FragmentActivity) :
        FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = urls.size
        override fun createFragment(position: Int): Fragment {
            return ImageViewerFragment.newInstance(urls[position])
        }
    }
}
