package com.nanoyatsu.nastodon.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.databinding.ActivityNavHostBinding

class NavHostActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityNavHostBinding>(this, R.layout.activity_nav_host)
        drawerLayout = binding.drawerLayout

        val navController = findNavController(R.id.main_fragment_container)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.main_fragment_container)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

}
