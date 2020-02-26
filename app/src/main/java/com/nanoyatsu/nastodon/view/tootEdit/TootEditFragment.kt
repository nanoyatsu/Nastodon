package com.nanoyatsu.nastodon.view.tootEdit


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.databinding.FragmentTootEditBinding

class TootEditFragment : Fragment() {
    private val vm by lazy {
        val tootComponent = (requireActivity().application as NastodonApplication).appComponent
            .nullableTootComponent().create(args.replyTo)
        tootComponent.viewModelFactory().create(TootEditViewModel::class.java)
    }
    private val args by lazy { TootEditFragmentArgs.fromBundle(arguments!!) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTootEditBinding.inflate(inflater, container, false)
            .also { initBinding(it) }
        return binding.root
    }

    private fun initBinding(binding: FragmentTootEditBinding) {
        // ViewModel設定
        // 画像追加
        vm.mediaAddEvent.observe(viewLifecycleOwner, Observer { if (it) addMedia(vm) })
        // トゥート送信イベント
        vm.tootSendEvent.observe(viewLifecycleOwner, Observer { if (it) sendToot(vm) })

        binding.vm = vm
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun addMedia(vm: TootEditViewModel) {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
        }
        startActivityForResult(i, Request.MEDIA_PICKER.ordinal)
    }

    private fun sendToot(vm: TootEditViewModel) {
        // todo キーボードをしまう
        val applicationContext = activity?.applicationContext
        vm.sendToot { Toast.makeText(applicationContext, it, Toast.LENGTH_LONG).show() }
        activity?.onBackPressed() // todo ナビゲーションで遷移
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) return
        when (Request.values()[requestCode]) {
            Request.MEDIA_PICKER -> if (resultCode == Activity.RESULT_OK) receiveMedia(data)
        }
    }

    private fun receiveMedia(intent: Intent) {
        Log.d("test", "msg")
        val uri = intent.data ?: return
        vm.addAttachment(uri)
    }

    companion object {
        enum class Request { MEDIA_PICKER }
    }
}
