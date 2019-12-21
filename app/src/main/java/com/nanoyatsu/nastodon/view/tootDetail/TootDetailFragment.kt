package com.nanoyatsu.nastodon.view.tootDetail


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
import com.nanoyatsu.nastodon.databinding.FragmentTootDetailBinding
import javax.inject.Inject

class TootDetailFragment : Fragment() {
    lateinit var binding: FragmentTootDetailBinding
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

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTootDetailBinding.inflate(inflater, container, false)
            .also { initBinding(it) }
        return binding.root
    }


    private fun initBinding(binding: FragmentTootDetailBinding) {
        val args = TootDetailFragmentArgs.fromBundle(arguments!!)
        val factory = TootViewModelFactory(args.toot, auth, apiManager)
        binding.vm = ViewModelProvider(this, factory).get(TootViewModel::class.java)
        binding.lifecycleOwner = this
    }
}