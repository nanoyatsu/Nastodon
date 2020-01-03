package com.nanoyatsu.nastodon.view.notice


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class NoticeFragment : Fragment() {
    lateinit var kind: NoticeViewModel.Kind

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
        // return inflater.inflate(R.layout.fragment_notice, container, false)
        return TextView(activity).apply { text = kind.name }
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
