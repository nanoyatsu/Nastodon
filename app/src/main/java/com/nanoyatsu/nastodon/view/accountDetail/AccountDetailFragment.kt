package com.nanoyatsu.nastodon.view.accountDetail

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
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.data.domain.Account
import com.nanoyatsu.nastodon.data.domain.Attachment
import com.nanoyatsu.nastodon.data.domain.Status
import com.nanoyatsu.nastodon.data.repository.account.AccountRepository
import com.nanoyatsu.nastodon.databinding.FragmentAccountDetailBinding
import com.nanoyatsu.nastodon.view.accountList.AccountListViewModel
import com.nanoyatsu.nastodon.view.timeline.TimelineAdapter
import com.nanoyatsu.nastodon.view.timeline.TimelineItemViewHolder
import kotlinx.android.synthetic.main.activity_nav_host.*
import javax.inject.Inject

class AccountDetailFragment : Fragment() {
    lateinit var binding: FragmentAccountDetailBinding
    @Inject
    lateinit var auth: AuthInfo
    @Inject
    lateinit var apiManager: MastodonApiManager

    lateinit var args: AccountDetailFragmentArgs

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity!!.application as NastodonApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        args = AccountDetailFragmentArgs.fromBundle(arguments!!)
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
        val repo = AccountRepository(apiManager.accounts, auth.accessToken, args.account.id)
        val factory = AccountViewModelFactory(args.account, repo)

        return ViewModelProvider(this, factory).get(AccountViewModel::class.java).apply {
            // RecyclerViewの更新監視
            val adapter = binding.toots.adapter as TimelineAdapter
            toots.observe(viewLifecycleOwner, Observer { adapter.submitList(it) })
            networkState.observe(viewLifecycleOwner, Observer { adapter.setNetworkState(it) })
            // SwipeRefreshの表示監視
            isInitialising
                .observe(viewLifecycleOwner, Observer { binding.swipeRefresh.isRefreshing = it })
            // フォロー動作 // review : vm側でcoroutineを投げており、finishedの実施箇所要再考
            followEvent.observe(viewLifecycleOwner, Observer {
                if (it) {
                    this.switchFollow()
                    this.onFollowClickFinished()
                }
            })
            // 遷移
            followingsEvent.observe(viewLifecycleOwner, Observer {
                if (it) {
                    transAccountList(args.account.id, AccountListViewModel.Kind.FOLLOWING)
                    onFollowingsClickFinished()
                }
            })
            followersEvent.observe(viewLifecycleOwner, Observer {
                if (it) {
                    transAccountList(args.account.id, AccountListViewModel.Kind.FOLLOWER)
                    onFollowersClickFinished()
                }
            })
        }
    }

    private fun transAccountList(accountId: String, kind: AccountListViewModel.Kind) {
        val directions = AccountDetailFragmentDirections
            .actionAccountDetailFragmentToAccountListFragment(accountId, kind)
        requireActivity().main_fragment_container.findNavController().navigate(directions)
    }

    private val navigation = object : TimelineItemViewHolder.Navigation {
        override fun transAccountDetail(account: Account) {
            if (account.id == this@AccountDetailFragment.args.account.id) return
            val directions = AccountDetailFragmentDirections
                .actionAccountDetailFragmentSelf(account)
            requireActivity().main_fragment_container.findNavController().navigate(directions)
        }

        override fun transTootEditAsReply(toot: Status) {
            val directions =
                AccountDetailFragmentDirections.actionAccountDetailFragmentToTootEditFragment(toot)
            requireActivity().main_fragment_container.findNavController().navigate(directions)
        }

        override fun transTootDetail(toot: Status) {
            val directions =
                AccountDetailFragmentDirections.actionAccountDetailFragmentToTootDetailFragment(toot)
            requireActivity().main_fragment_container.findNavController().navigate(directions)
        }

        override fun transImagePager(contents: List<Attachment>) {
            val urls = contents.map { it.url }.toTypedArray()
            val directions =
                AccountDetailFragmentDirections.actionAccountDetailFragmentToImagePagerFragment(urls)
            requireActivity().main_fragment_container.findNavController().navigate(directions)
        }
    }
}