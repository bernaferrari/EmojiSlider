package com.bernaferrari.emojislidersample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.nav_frag.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nav_frag)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment
        NavigationUI.setupWithNavController(bottomNavView, navHostFragment.navController)
    }
}

