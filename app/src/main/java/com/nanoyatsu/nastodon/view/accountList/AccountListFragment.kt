package com.nanoyatsu.nastodon.view.accountList

import android.content.Context
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
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.data.domain.Account
import com.nanoyatsu.nastodon.databinding.FragmentAccountListBinding
import com.nanoyatsu.nastodon.view.accountDetail.AccountItemViewHolder
import kotlinx.android.synthetic.main.activity_nav_host.*
import javax.inject.Inject

class AccountListFragment : Fragment() {
    private lateinit var binding: FragmentAccountListBinding
    @Inject
    lateinit var auth: AuthInfo
    @Inject
    lateinit var apiManager: MastodonApiManager

    private lateinit var args: AccountListFragmentArgs

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as NastodonApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        args = AccountListFragmentArgs.fromBundle(requireArguments())
        binding = FragmentAccountListBinding.inflate(inflater, container, false)
            .also { initBinding(it) }
        return binding.root
    }

    private fun initBinding(binding: FragmentAccountListBinding) {
        val context = requireContext()

        // 描画設定
        binding.accountList.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.accountList.adapter = AccountAdapter(context, navigation)

        // ViewModel設定
        val vm = generateViewModel(binding)
        binding.vm = vm
        binding.lifecycleOwner = this

        // イベント設定
        // SwipeRefresh
        binding.swipeRefresh.setOnRefreshListener { vm.refresh() }
    }

    private fun generateViewModel(binding: FragmentAccountListBinding): AccountListViewModel {
        val component = (requireActivity().application as NastodonApplication).appComponent
            .accountComponent().create(args.account).accountListComponent().create(args.kind)
        val vm = component.viewModelFactory().create(AccountListViewModel::class.java)

        return vm.apply {
            // RecyclerViewの更新監視
            val adapter = binding.accountList.adapter as AccountAdapter
            accounts.observe(viewLifecycleOwner, Observer { adapter.submitList(it) })
            networkState.observe(viewLifecycleOwner, Observer { adapter.setNetworkState(it) })
            // SwipeRefreshの表示監視
            isInitialising
                .observe(viewLifecycleOwner, Observer { binding.swipeRefresh.isRefreshing = it })
        }

    }

    val navigation = object : AccountItemViewHolder.Navigation {
        override fun transAccountDetail(account: Account) {
            val directions = AccountListFragmentDirections
                .actionAccountListFragmentToAccountDetailFragment(account)
            requireActivity().main_fragment_container.findNavController().navigate(directions)
        }
    }
}
