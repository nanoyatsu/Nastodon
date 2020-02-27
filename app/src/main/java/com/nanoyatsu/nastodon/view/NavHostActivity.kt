package com.nanoyatsu.nastodon.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.database.NastodonDataBase
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.data.domain.Application
import com.nanoyatsu.nastodon.databinding.ActivityNavHostBinding
import com.nanoyatsu.nastodon.view.auth.AuthActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import javax.inject.Inject


class NavHostActivity : AppCompatActivity() {
    lateinit var binding: ActivityNavHostBinding

    @Inject
    lateinit var db: NastodonDataBase
    @Inject
    lateinit var apiManager: MastodonApiManager

    private val hasBottomNavFragments =
        arrayOf(R.id.timelineFrameFragment, R.id.noticeFrameFragment, R.id.searchFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        (this.application as NastodonApplication).appComponent.inject(this)
        checkAuth()

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nav_host)

        val navController = findNavController(R.id.main_fragment_container)
//        val navController = Navigation.findNavController(binding.mainFragmentContainer) // navigationを2.2.0にしてFragmentContainerViewに変える時に使う
        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        navController.addOnDestinationChangedListener { navController: NavController, navDestination: NavDestination, bundle: Bundle? ->
            // ここに認証確認して戻す処理？ 調べる
        }

        // 下部ナビゲーションの表示制御
        navController.addOnDestinationChangedListener(::setBottomNavVisibility)

        // 下部ナビゲーションメニューのIDとナビゲーショングラフのIDの紐付け
        NavigationUI.setupWithNavController(binding.bottomNavView, navController)

        requestPermissions()
    }

    private fun setBottomNavVisibility(_1: NavController, dest: NavDestination, _2: Bundle?) {
        if (dest.id in hasBottomNavFragments)
            binding.bottomNavView.visibility = View.VISIBLE
        else
            binding.bottomNavView.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.main_fragment_container)
        return NavigationUI.navigateUp(navController, binding.drawerLayout)
    }

    // todo リポジトリに分離
    private fun checkAuth() {
        val auth = runBlocking(Dispatchers.IO) { db.authInfoDao().getAll().firstOrNull() }

        if (auth !is AuthInfo || !hasAuthInfo(auth) || !verifyCredentials(auth)) {
            // 認証に行く
            // いまのところ別Activity
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun hasAuthInfo(auth: AuthInfo): Boolean {
        if (auth.instanceUrl.isEmpty()) return false
        if (auth.accessToken.isEmpty()) return false
        return true
    }

    private fun verifyCredentials(auth: AuthInfo): Boolean {
        val api = apiManager.apps
        return runBlocking {
            try {
                val res = api.verifyCredentials(auth.accessToken)
                val apps = res.body()?.asDomainModel()
                // nameも一致するか確認
                return@runBlocking apps is Application && apps.name == getString(R.string.app_name)
            } catch (e: HttpException) {
                e.printStackTrace()
                return@runBlocking false
            }
            // todo IOExceptionも受ける
        }
    }

    private fun requestPermissions() {
        PermissionRequest.values().map { request ->
            val check = request.permissions.map { ContextCompat.checkSelfPermission(this, it) }
            if (check.contains(PackageManager.PERMISSION_DENIED))
                ActivityCompat.requestPermissions(this, request.permissions, request.code.ordinal)
        }
    }

    companion object {
        // ordinalをonActivityResult系のRequestCodeに使うEnum
        enum class Request { PERMISSION_EXTERNAL_STORAGE }

        // Permission系のリクエストをまとめたEnum
        enum class PermissionRequest(val code: Request, val permissions: Array<String>) {
            EXTERNAL_STORAGE(
                Request.PERMISSION_EXTERNAL_STORAGE,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }
}
