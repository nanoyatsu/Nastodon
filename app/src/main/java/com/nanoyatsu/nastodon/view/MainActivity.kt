package com.nanoyatsu.nastodon.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.model.Status
import com.nanoyatsu.nastodon.presenter.ApiClient
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        mainText.setOnClickListener { callApi() }
    }

    override fun onResume() {
        // todo とりあえず動かしたのでそろそろ記載箇所を分ける
        super.onResume()

        val gson: Gson = GsonBuilder().let {
            it.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            it.create()
        }

        val retrofit = Retrofit.Builder().let {
            it.baseUrl("https://qiitadon.com/")
            it.addConverterFactory(GsonConverterFactory.create(gson))
            it.build()
        }
        val response = retrofit.create(ApiClient::class.java)
        val publicTimeline = response.getPublicTimelines()
        publicTimeline.enqueue(object : Callback<Array<Status>> {
            override fun onResponse(call: Call<Array<Status>>, response: Response<Array<Status>>) {
                val list = response.body()
                if (list != null) {
                    val adapter = TimelineAdapter(baseContext, 1, list)
                    adapter.notifyDataSetChanged()
                    timelineView.adapter = adapter
                }
            }

            override fun onFailure(call: Call<Array<Status>>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun callApi() {
        val gson: Gson = GsonBuilder().let {
            it.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            it.create()
        }

        val retrofit = Retrofit.Builder().let {
            it.baseUrl("https://qiitadon.com/")
            it.addConverterFactory(GsonConverterFactory.create(gson))
            it.build()
        }
        val response = retrofit.create(ApiClient::class.java)
        val firstTootCall: Call<Status> = response.getNanoFirstToot()
        firstTootCall.enqueue(object : Callback<Status> {
            override fun onResponse(call: Call<Status>, response: Response<Status>) {
                mainText.text = response.body()?.content
            }

            override fun onFailure(call: Call<Status>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
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
                // Handle the camera action
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
