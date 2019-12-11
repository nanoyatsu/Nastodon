package com.nanoyatsu.nastodon.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.entity.Apps
import com.nanoyatsu.nastodon.data.database.NastodonDataBase
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.databinding.ActivityNavHostBinding
import com.nanoyatsu.nastodon.view.auth.AuthActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import javax.inject.Inject

class NavHostActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout

    @Inject
    lateinit var db: NastodonDataBase
    @Inject
    lateinit var apiManager: MastodonApiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        (this.application as NastodonApplication).appComponent.inject(this)
        checkAuth()

        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityNavHostBinding>(this, R.layout.activity_nav_host)
        drawerLayout = binding.drawerLayout

        val navController = findNavController(R.id.main_fragment_container)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        navController.addOnDestinationChangedListener { navController: NavController, navDestination: NavDestination, bundle: Bundle? ->
            // ここに認証確認して戻す処理？ 調べる
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.main_fragment_container)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

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
