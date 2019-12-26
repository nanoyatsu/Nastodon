package com.nanoyatsu.nastodon.view.tootEdit


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.entity.Visibility
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.databinding.FragmentTootEditBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (auth.instanceUrl == "") {
//            finish() // todo 認証に行く
//            return
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTootEditBinding.inflate(inflater, container, false)
            .also { initBinding(it) }
        return binding.root
    }

    private fun initBinding(binding: FragmentTootEditBinding) {
        val factory = TootEditViewModelFactory(auth, apiManager)
        binding.vm = ViewModelProvider(this, factory).get(TootEditViewModel::class.java)

        val adapter = ArrayAdapter(
            activity!!,
            R.layout.support_simple_spinner_dropdown_item,
            Visibility.values().map { it.label })

        binding.visibilitySpinner.adapter = adapter
        binding.buttonSend.setOnClickListener { sendToot() }
    }

    private fun sendToot() {
        val paramVisibility = Visibility.values()[binding.visibilitySpinner.selectedItemPosition]

        CoroutineScope(context = Dispatchers.Main).launch {
            try {
                val res = apiManager.statuses.postToot(
                    authorization = auth.accessToken,
                    status = binding.content.text.toString(),
                    visibility = paramVisibility.name.toLowerCase(Locale.ROOT)
                )
                Log.d(
                    this@TootEditFragment.javaClass.simpleName,
                    res.body()?.toString() ?: res.errorBody().toString()
                )
                // {"error": "アクセストークンは取り消されています"} 消してたらこうなる
                activity?.onBackPressed()
            } catch (e: HttpException) {
                e.printStackTrace()
            }
        }
    }

}
