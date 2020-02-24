package com.nanoyatsu.nastodon.view.tootDetail


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.data.domain.Attachment
import com.nanoyatsu.nastodon.databinding.FragmentTootDetailBinding
import kotlinx.android.synthetic.main.activity_nav_host.*

class TootDetailFragment : Fragment() {
    val args by lazy { TootDetailFragmentArgs.fromBundle(arguments!!) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTootDetailBinding.inflate(inflater, container, false)
            .also { initBinding(it) }
        return binding.root
    }

    private fun initBinding(binding: FragmentTootDetailBinding) {
        // 描画設定
        setupAttachments(requireActivity(), binding.attachments, args.toot.mediaAttachments)

        // ViewModel設定
        val tootComponent = (requireActivity().application as NastodonApplication).appComponent
            .tootComponent().create(args.toot)
        val vm = tootComponent.viewModelFactory().create(TootViewModel::class.java)
        vm.avatarClickEvent.observe(viewLifecycleOwner, Observer { if (it) onAvatarClick(vm) })
        vm.replyEvent.observe(viewLifecycleOwner, Observer { if (it) onReplyClick(vm) })
        vm.reblogEvent.observe(viewLifecycleOwner, Observer { if (it) vm.doReblog() })
        vm.favouriteEvent.observe(viewLifecycleOwner, Observer { if (it) vm.doFav() })

        binding.vm = vm
        binding.lifecycleOwner = this
    }

    private fun setupAttachments(context: Context, view: RecyclerView, contents: List<Attachment>) {
        view.layoutManager = GridLayoutManager(context, 2)
        view.adapter = MediaAttachmentAdapter(contents.toTypedArray())
            .apply {
                publicListener = object : MediaAttachmentAdapter.ThumbnailClickListener {
                    override val onThumbnailClick = { onAttachmentClick(contents) }
                }
            }
    }

    private fun onAvatarClick(vm: TootViewModel) {
        val directions =
            TootDetailFragmentDirections.actionTootDetailFragmentToAccountDetailFragment(vm.toot.value!!.account)
        requireActivity().main_fragment_container.findNavController().navigate(directions)
        vm.onAvatarClickFinished()
    }

    private fun onReplyClick(vm: TootViewModel) {
        val directions =
            TootDetailFragmentDirections.actionTootDetailFragmentToTootEditFragment(vm.toot.value!!)
        requireActivity().main_fragment_container.findNavController().navigate(directions)
        vm.onReplyClickFinished()
    }

    private fun onAttachmentClick(contents: List<Attachment>) {
        val urls = contents.map { it.url }.toTypedArray()
        val directions =
            TootDetailFragmentDirections.actionTootDetailFragmentToImagePagerFragment(urls)
        requireActivity().main_fragment_container.findNavController().navigate(directions)
    }
}