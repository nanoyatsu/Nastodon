package com.nanoyatsu.nastodon.view.tootEdit


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.databinding.FragmentTootEditBinding

class TootEditFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTootEditBinding.inflate(inflater, container, false)
            .also { initBinding(it) }
        return binding.root
    }

    private fun initBinding(binding: FragmentTootEditBinding) {
        val args = TootEditFragmentArgs.fromBundle(arguments!!)

        // ViewModel設定
        val tootComponent = (requireActivity().application as NastodonApplication).appComponent
            .nullableTootComponent().create(args.replyTo)
        val vm = tootComponent.viewModelFactory().create(TootEditViewModel::class.java)
        // 警告投稿 review : Switch値のVisibilityへの反映がXML側だけで対応出来ない？
        vm.isContentWarning.observe(viewLifecycleOwner, Observer
        { binding.cwContent.visibility = if (it) View.VISIBLE else View.GONE })
        vm.liveReplyTo.observe(viewLifecycleOwner, Observer
        { binding.frameReply.visibility = if (it != null) View.VISIBLE else View.GONE })

        // トゥート送信イベント
        vm.tootSendEvent.observe(viewLifecycleOwner, Observer { if (it) sendToot(vm) })

        binding.vm = vm
        binding.lifecycleOwner = this
    }


    private fun sendToot(vm: TootEditViewModel) {
        // todo キーボードをしまう
        val applicationContext = activity?.applicationContext
        vm.sendToot { Toast.makeText(applicationContext, it, Toast.LENGTH_LONG).show() }
        activity?.onBackPressed() // todo ナビゲーションで遷移
    }
}
