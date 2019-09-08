package com.nanoyatsu.nastodon.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.model.Apps
import com.nanoyatsu.nastodon.model.AuthPreferenceManager
import com.nanoyatsu.nastodon.model.Status
import com.nanoyatsu.nastodon.presenter.MastodonApiManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException

// todo 起動→認証確認→(バック履歴クリア)→認証画面orタイムライン画面 にする(今はタイムライン画面に同居)
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val pref = AuthPreferenceManager(this@MainActivity)
        // 認証有無の確認　// todo notがダサめ
        if (!(hasAuthInfo(pref) && verifyCredentials(pref))) {
            startAuthDialog()
            return
        }

        // todo 初期タイムラインへの遷移(まだ画面もない)

        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, TootEditActivity::class.java)
            startActivity(intent)
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    fun startAuthDialog() {
        // todo 認証画面に行く
    }

    fun hasAuthInfo(pref: AuthPreferenceManager): Boolean {
        if (pref.instanceUrl == "")
            return false
        if (pref.accessToken == "")
            return false
        return true
    }

    fun verifyCredentials(pref: AuthPreferenceManager): Boolean {
        val api = MastodonApiManager(pref.instanceUrl).api
        var result = false
        runBlocking {
            result = try {
                val res = api.verifyCredentials(pref.accessToken)
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

    override fun onResume() {
        super.onResume()
        val pref = AuthPreferenceManager(this@MainActivity)
        if (pref.instanceUrl == "")
            return

        reloadPublicTimeline(pref.instanceUrl)
    }

    private fun reloadPublicTimeline(url: String) {
        val api = MastodonApiManager(url).api
        CoroutineScope(context = Dispatchers.Main).launch {
            try {
                val res = api.getPublicTimeline(local = true)
                val toots = res.body()
                if (toots is Array<Status>) {
                    val adapter = TimelineAdapter(baseContext, toots)
                    adapter.notifyDataSetChanged()
                    timelineView.adapter = adapter
                } else {
//                    res.errorBody() でなんかする
                }
            } catch (e: HttpException) {
                e.printStackTrace()
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this@MainActivity, AuthActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_tools -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
