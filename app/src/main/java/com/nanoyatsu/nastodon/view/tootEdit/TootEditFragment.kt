package com.nanoyatsu.nastodon.view.tootEdit


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.entity.Visibility
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.databinding.FragmentTootEditBinding
import javax.inject.Inject

class TootEditFragment : Fragment() {
    lateinit var binding: FragmentTootEditBinding
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
        binding = FragmentTootEditBinding.inflate(inflater, container, false)
            .also { initBinding(it) }
        return binding.root
    }

    private fun initBinding(binding: FragmentTootEditBinding) {
        val args = TootEditFragmentArgs.fromBundle(arguments!!)
        val factory = TootEditViewModelFactory(args.replyTo, auth, apiManager)
        val vm = ViewModelProvider(this, factory).get(TootEditViewModel::class.java)
        binding.vm = vm


        // 警告投稿 review : Switch値のVisibilityへの反映がXML側だけで対応出来ない？
        vm.isContentWarning.observe(viewLifecycleOwner, Observer
        { binding.cwContent.visibility = if (it) View.VISIBLE else View.GONE })
        vm.liveReplyTo.observe(viewLifecycleOwner, Observer
        { binding.frameReply.visibility = if (it != null) View.VISIBLE else View.GONE })

        // トゥート送信
        vm.tootSendEvent.observe(viewLifecycleOwner, Observer { if (it) sendToot() })
    }


    private fun sendToot() {
        // todo キーボードをしまう
        val applicationContext = activity?.applicationContext
        binding.vm?.sendToot(Visibility.values()[binding.visibilitySpinner.selectedItemPosition])
        { Toast.makeText(applicationContext, it, Toast.LENGTH_LONG).show() }
        activity?.onBackPressed() // todo ナビゲーションで遷移
    }
}
