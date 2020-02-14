package com.nanoyatsu.nastodon.view.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.data.domain.Account
import com.nanoyatsu.nastodon.data.domain.Attachment
import com.nanoyatsu.nastodon.data.domain.Status
import com.nanoyatsu.nastodon.databinding.FragmentTimelineBinding
import kotlinx.android.synthetic.main.activity_nav_host.*

class TimelineFragment : Fragment() {

    private lateinit var binding: FragmentTimelineBinding
    var args = Args()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val kind = TimelineViewModel.Kind.values().getOrNull(it.getInt(ARG_KEY_KIND))
                ?: TimelineViewModel.Kind.HOME
            args = Args(kind)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimelineBinding.inflate(inflater, container, false)
            .also { initBinding(it) }
        return binding.root
    }

    private fun initBinding(binding: FragmentTimelineBinding) {
        val context = requireContext()

        // 描画設定
        // RecyclerView
        binding.timelineView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)// fixme 画面回転を連続したりするとNPE
        binding.timelineView.adapter = TimelineAdapter(context, navigation)

        // ViewModel設定
        val vm = generateViewModel(binding)
        binding.vm = vm
        binding.lifecycleOwner = this

        // イベント設定
        // SwipeRefresh
        binding.swipeRefresh.setOnRefreshListener { vm.refresh() }
    }

    private fun generateViewModel(binding: FragmentTimelineBinding): TimelineViewModel {
        val timelineComponent = (requireActivity().application as NastodonApplication).appComponent
            .timelineComponent().create(args.kind)

        return timelineComponent.viewModelFactory().create(TimelineViewModel::class.java).apply {
            // Timelineの常時更新
            val adapter = binding.timelineView.adapter as TimelineAdapter
            statuses.observe(viewLifecycleOwner, Observer { adapter.submitList(it) })
            networkState.observe(viewLifecycleOwner, Observer { adapter.setNetworkState(it) })
            // SwipeRefreshの表示監視
            isInitialising
                .observe(viewLifecycleOwner, Observer { binding.swipeRefresh.isRefreshing = it })
        }
    }

    private val navigation = object : TimelineItemViewHolder.Navigation {
        override fun transAccountDetail(account: Account) {
            val directions = TimelineFrameFragmentDirections
                .actionTimelineFrameFragmentToAccountDetailFragment(account)
            requireActivity().main_fragment_container.findNavController().navigate(directions)
        }

        override fun transTootEditAsReply(toot: Status) {
            val directions =
                TimelineFrameFragmentDirections.actionTimelineFrameFragmentToTootEditFragment(toot)
            requireActivity().main_fragment_container.findNavController().navigate(directions)
        }

        override fun transTootDetail(toot: Status) {
            val directions =
                TimelineFrameFragmentDirections.actionTimelineFrameFragmentToTootDetailFragment(toot)
            requireActivity().main_fragment_container.findNavController().navigate(directions)
        }

        override fun transImagePager(contents: List<Attachment>) {
            val urls = contents.map { it.url }.toTypedArray()
            val directions =
                TimelineFrameFragmentDirections.actionTimelineFrameFragmentToImagePagerFragment(urls)
            requireActivity().main_fragment_container.findNavController().navigate(directions)
        }
    }

    fun focusTop() {
        binding.timelineView.smoothScrollToPosition(0)
    }

    companion object {
        const val ARG_KEY_KIND = "KIND"
        fun newInstance(kind: TimelineViewModel.Kind) =
            TimelineFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_KEY_KIND, kind.ordinal)
                }
            }

        data class Args(
            val kind: TimelineViewModel.Kind = TimelineViewModel.Kind.HOME
        )
    }
}