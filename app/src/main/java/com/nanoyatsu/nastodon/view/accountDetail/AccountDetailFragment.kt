package com.nanoyatsu.nastodon.view.accountDetail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.data.domain.Attachment
import com.nanoyatsu.nastodon.data.domain.Status
import com.nanoyatsu.nastodon.data.repository.accountToots.AccountTootsRepository
import com.nanoyatsu.nastodon.databinding.FragmentAccountDetailBinding
import com.nanoyatsu.nastodon.view.timeline.TimelineAdapter
import com.nanoyatsu.nastodon.view.timeline.TimelineItemViewHolder
import javax.inject.Inject

class AccountDetailFragment : Fragment() {
    lateinit var binding: FragmentAccountDetailBinding
    @Inject
    lateinit var auth: AuthInfo
    @Inject
    lateinit var apiManager: MastodonApiManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity!!.application as NastodonApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountDetailBinding.inflate(inflater, container, false)
            .also { initBinding(it) }
        return binding.root
    }

    private fun initBinding(binding: FragmentAccountDetailBinding) {
        val context = requireContext()

        // 描画設定
        // RecyclerView
        binding.toots.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.toots.adapter = TimelineAdapter(context, navigation)

        // ViewModel設定
        val vm = generateViewModel(binding)
        binding.vm = vm
        binding.lifecycleOwner = this

        // イベント設定
        // SwipeRefresh
        binding.swipeRefresh.setOnRefreshListener { vm.refresh() }
    }

    private fun generateViewModel(binding: FragmentAccountDetailBinding): AccountViewModel {
        val args = AccountDetailFragmentArgs.fromBundle(arguments!!)
        val repo = AccountTootsRepository(apiManager.accounts, auth.accessToken, args.account.id)
        val factory = AccountViewModelFactory(args.account, repo)

        return ViewModelProvider(this, factory).get(AccountViewModel::class.java).apply {
            // RecyclerViewの更新監視
            val adapter = binding.toots.adapter as TimelineAdapter
            toots.observe(viewLifecycleOwner, Observer { adapter.submitList(it) })
            networkState.observe(viewLifecycleOwner, Observer { adapter.setNetworkState(it) })
            // SwipeRefreshの表示監視
            isInitialising
                .observe(viewLifecycleOwner, Observer { binding.swipeRefresh.isRefreshing = it })
        }
    }

    private val navigation = object : TimelineItemViewHolder.Navigation {
        // todo こいつら
        override fun transTootEditAsReply(toot: Status) {}

        override fun transTootDetail(toot: Status) {}

        override fun transImagePager(contents: List<Attachment>) {}

    }
}