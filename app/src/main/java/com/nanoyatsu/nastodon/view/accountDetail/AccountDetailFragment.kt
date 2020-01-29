package com.nanoyatsu.nastodon.view.accountDetail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.databinding.FragmentAccountDetailBinding
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
        val args = AccountDetailFragmentArgs.fromBundle(arguments!!)
        val factory = AccountViewModelFactory(args.account)

        val vm = ViewModelProvider(this, factory).get(AccountViewModel::class.java)

        binding.vm = vm
    }
}