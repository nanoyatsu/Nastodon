package com.nanoyatsu.nastodon.view.notice


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.database.dao.NoticeDao
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.data.domain.Account
import com.nanoyatsu.nastodon.data.domain.Attachment
import com.nanoyatsu.nastodon.data.domain.Status
import com.nanoyatsu.nastodon.data.repository.notice.NoticeRepository
import com.nanoyatsu.nastodon.databinding.FragmentNoticeBinding
import com.nanoyatsu.nastodon.view.timeline.TimelineItemViewHolder
import kotlinx.android.synthetic.main.activity_nav_host.*
import javax.inject.Inject

class NoticeFragment : Fragment() {

    private lateinit var binding: FragmentNoticeBinding
    lateinit var kind: NoticeViewModel.Kind

    @Inject
    lateinit var auth: AuthInfo
    @Inject
    lateinit var noticeDao: NoticeDao
    @Inject
    lateinit var apiManager: MastodonApiManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity!!.application as NastodonApplication).appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            kind = NoticeViewModel.Kind.values().getOrNull(it.getInt(ARG_KEY_KIND))
                ?: NoticeViewModel.Kind.ALL
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoticeBinding.inflate(inflater, container, false)
            .also { initBinding(it) }
        return binding.root
    }

    private fun initBinding(binding: FragmentNoticeBinding) {
        val context = requireContext()

        // 描画設定
        // RecyclerView
        binding.noticeView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.noticeView.adapter = NoticeAdapter(context, noticeNavigation, tootNavigation)

        // ViewModel設定
        val vm = generateViewModel(binding)
        binding.vm = vm
        binding.lifecycleOwner = this

        // イベント設定
        // SwipeRefresh
        binding.swipeRefresh.setOnRefreshListener { vm.refresh() }
    }

    private fun generateViewModel(binding: FragmentNoticeBinding): NoticeViewModel {
        val repo = NoticeRepository(kind, noticeDao, apiManager.notifications, auth.accessToken)
        val factory = NoticeViewModelFactory(repo)

        return ViewModelProvider(this, factory).get(NoticeViewModel::class.java).apply {
            // RecyclerViewの更新監視
            val adapter = binding.noticeView.adapter as NoticeAdapter
            notifications.observe(viewLifecycleOwner, Observer { adapter.submitList(it) })
            networkState.observe(viewLifecycleOwner, Observer { adapter.setNetworkState(it) })
            // SwipeRefreshの表示監視
            isInitialising
                .observe(viewLifecycleOwner, Observer { binding.swipeRefresh.isRefreshing = it })
        }
    }


    private val noticeNavigation = object : NoticeItemViewHolder.Navigation {
        override fun transAccountDetail(account: Account) =
            this@NoticeFragment.transAccountDetail(account)

        override fun transTootDetail(toot: Status) = this@NoticeFragment.transTootDetail(toot)
    }

    private val tootNavigation = object : TimelineItemViewHolder.Navigation {
        override fun transAccountDetail(account: Account) =
            this@NoticeFragment.transAccountDetail(account)

        override fun transTootEditAsReply(toot: Status) =
            this@NoticeFragment.transTootEditAsReply(toot)

        override fun transTootDetail(toot: Status) = this@NoticeFragment.transTootDetail(toot)

        override fun transImagePager(contents: List<Attachment>) =
            this@NoticeFragment.transImagePager(contents)
    }

    private fun transAccountDetail(account: Account) {
        val directions = NoticeFrameFragmentDirections
            .actionNoticeFrameFragmentToAccountDetailFragment(account)
        requireActivity().main_fragment_container.findNavController().navigate(directions)
    }

    private fun transTootEditAsReply(toot: Status) {
        val directions =
            NoticeFrameFragmentDirections.actionNoticeFrameFragmentToTootEditFragment(toot)
        requireActivity().main_fragment_container.findNavController().navigate(directions)
    }

    private fun transTootDetail(toot: Status) {
        val directions =
            NoticeFrameFragmentDirections.actionNoticeFrameFragmentToTootDetailFragment(toot)
        requireActivity().main_fragment_container.findNavController().navigate(directions)
    }

    private fun transImagePager(contents: List<Attachment>) {
        val urls = contents.map { it.url }.toTypedArray()
        val directions =
            NoticeFrameFragmentDirections.actionNoticeFrameFragmentToImagePagerFragment(urls)
        requireActivity().main_fragment_container.findNavController().navigate(directions)
    }

    companion object {
        private const val ARG_KEY_KIND = "KIND"
        fun newInstance(kind: NoticeViewModel.Kind) =
            NoticeFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_KEY_KIND, kind.ordinal)
                }
            }
    }
}
