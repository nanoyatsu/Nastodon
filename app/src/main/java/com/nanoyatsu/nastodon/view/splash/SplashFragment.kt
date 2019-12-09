package com.nanoyatsu.nastodon.view.splash


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.entity.Apps
import com.nanoyatsu.nastodon.data.database.NastodonDataBase
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.view.auth.AuthActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import javax.inject.Inject

/**
 * 認証情報の有無を確認して移動先を判定
 * （Timeline or Auth）
 */
class SplashFragment : Fragment() {

    @Inject
    lateinit var db: NastodonDataBase
    @Inject
    lateinit var apiManager: MastodonApiManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity!!.application as NastodonApplication).appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 画面つくる→即抜けでいい
        CoroutineScope(Dispatchers.IO).launch {
            val auth = db.authInfoDao().getAll().firstOrNull()

            if (auth is AuthInfo && hasAuthInfo(auth) && verifyCredentials(auth)) {
                runBlocking(Dispatchers.Main) {
                    findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToTimelineFrameFragment())
                }
            } else {
                // 認証に行く
                // いまのところ別Activity
                val intent = Intent(activity, AuthActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    private fun hasAuthInfo(auth: AuthInfo): Boolean {
        if (auth.instanceUrl.isEmpty())
            return false
        if (auth.accessToken.isEmpty())
            return false
        return true
    }

    private fun verifyCredentials(auth: AuthInfo): Boolean {
        val api = apiManager.apps
        var result = false
        runBlocking {
            result = try {
                val res = api.verifyCredentials(auth.accessToken)
                val apps = res.body()
                // nameも一致するか確認
                apps is Apps && apps.name == getString(R.string.app_name)
            } catch (e: HttpException) {
                e.printStackTrace()
                false
            }
        }
        return result
    }
}
