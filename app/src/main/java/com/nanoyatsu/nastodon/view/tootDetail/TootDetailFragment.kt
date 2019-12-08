package com.nanoyatsu.nastodon.view.tootDetail


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.R
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
        val args = TootDetailFragmentArgs.fromBundle(arguments!!)

        binding = DataBindingUtil.setContentView<FragmentTootDetailBinding>(
            activity!!, R.layout.fragment_toot_detail
        ).also {
            val factory = TootViewModelFactory(args.toot, auth, apiManager)
            it.vm = ViewModelProvider(this@TootDetailFragment, factory)
                .get(TootViewModel::class.java)
            it.lifecycleOwner = this@TootDetailFragment
        }

        return inflater.inflate(R.layout.fragment_toot_detail, container, false)
    }
}
