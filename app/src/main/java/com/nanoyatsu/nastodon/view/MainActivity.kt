package com.nanoyatsu.nastodon.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.model.AuthPreferenceManager
import com.nanoyatsu.nastodon.model.Status
import com.nanoyatsu.nastodon.presenter.MastodonApiManager
import com.nanoyatsu.nastodon.view.adapter.TimelineAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        floating_edit.setOnClickListener {
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


    override fun onResume() {
        super.onResume()
        val pref = AuthPreferenceManager(this@MainActivity)
        if (pref.instanceUrl == "")
            return

        progress_view.visibility = View.VISIBLE
        CoroutineScope(context = Dispatchers.Main).launch {
            reloadPublicTimeline(pref.instanceUrl)
            progress_view.visibility = View.GONE
        }
    }

    private suspend fun reloadPublicTimeline(url: String) {
        val api = MastodonApiManager(url).timelines
        val response = CoroutineScope(context = Dispatchers.IO).async {
            try {
                val res = api.getPublicTimeline(local = true)
                res.body()
                // todo レスポンスが期待通りじゃないとき
            } catch (e: HttpException) {
                e.printStackTrace()
                null
            }
        }
        val toots = response.await()
        if (toots is Array<Status>) {
            val layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            timelineView.layoutManager = layoutManager

            val toArrayList = arrayListOf<Status>().also { it.addAll(toots) }
            val adapter = TimelineAdapter(baseContext, toArrayList)
            timelineView.adapter = adapter
            adapter.notifyDataSetChanged()
        } else {
            // ここだと res.errorBody() できないのでまた考える
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
