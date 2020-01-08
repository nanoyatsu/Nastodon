package com.nanoyatsu.nastodon.view.notice


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
import com.nanoyatsu.nastodon.databinding.FragmentNoticeBinding
import javax.inject.Inject

class NoticeFragment : Fragment() {

    private lateinit var binding: FragmentNoticeBinding
    lateinit var kind: NoticeViewModel.Kind

    @Inject
    lateinit var auth: AuthInfo
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
        val factory = NoticeViewModelFactory(kind, auth, apiManager)
        val context = this.context ?: return
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.noticeView.layoutManager = layoutManager
        val adapter = NoticeAdapter(context)
        binding.noticeView.adapter = adapter

        val vm = ViewModelProvider(this, factory).get(NoticeViewModel::class.java)
        // リストの常時更新
        vm.notifications.observe(viewLifecycleOwner, Observer { adapter.submitList(it) })
        vm.networkState.observe(viewLifecycleOwner, Observer { adapter.setNetworkState(it) })

        // SwipeRefresh
        vm.isInitialising.observe(
            viewLifecycleOwner,
            Observer { binding.swipeRefresh.isRefreshing = it })

        binding.vm = vm
        binding.lifecycleOwner = this
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
