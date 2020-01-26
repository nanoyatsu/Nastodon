package com.nanoyatsu.nastodon.view.notice


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.entity.Attachment
import com.nanoyatsu.nastodon.data.database.dao.NoticeDao
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.data.entity.Status
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
        val repo = NoticeRepository(kind, noticeDao, apiManager.notifications, auth.accessToken)
        val factory = NoticeViewModelFactory(repo)
        val context = this.context ?: return
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.noticeView.layoutManager = layoutManager
        val adapter = NoticeAdapter(context, noticeNavigation, tootNavigation)
        binding.noticeView.adapter = adapter

        val vm = ViewModelProvider(this, factory).get(NoticeViewModel::class.java)
        // リストの常時更新
        vm.notifications.observe(viewLifecycleOwner, Observer { adapter.submitList(it) })
        vm.networkState.observe(viewLifecycleOwner, Observer { adapter.setNetworkState(it) })

        // SwipeRefresh
        binding.swipeRefresh.setOnRefreshListener { vm.refreshNotifications() }
        vm.isInitialising.observe(
            viewLifecycleOwner,
            Observer { binding.swipeRefresh.isRefreshing = it })

        binding.vm = vm
        binding.lifecycleOwner = this
    }

    private val noticeNavigation = null // todo NoticeItemViewHolder.Navigation 実装

    private val tootNavigation = object : TimelineItemViewHolder.Navigation {
        //    // todo : navigation対応
        //    private fun transAccountPage(v: View, account: Account) {
        //        val intent = Intent(context, AccountPageActivity::class.java)
        //            .also { it.putExtra(AccountPageActivity.IntentKey.ACCOUNT.name, account) }
        //        v.context.startActivity(intent)
        //    }

        private fun navigate(directions: NavDirections) {
            val activity = requireNotNull(activity)
            activity.main_fragment_container.findNavController().navigate(directions)
        }

        override fun transTootEditAsReply(toot: Status) {
            val directions =
                NoticeFrameFragmentDirections.actionNoticeFrameFragmentToTootEditFragment(toot)
            navigate(directions)
        }

        override fun transTootDetail(toot: Status) {
            val directions =
                NoticeFrameFragmentDirections.actionNoticeFrameFragmentToTootDetailFragment(toot)
            navigate(directions)
        }

        override fun transImagePager(contents: List<Attachment>) {
            val urls = contents.map { it.url }.toTypedArray()
            val directions =
                NoticeFrameFragmentDirections.actionNoticeFrameFragmentToImagePagerFragment(urls)
            navigate(directions)
        }
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
