package com.chartracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // hides "up" button for the following fragments
        val appBarConfiguration = AppBarConfiguration
            .Builder(
                R.id.emailVerificationFragment,
                R.id.storiesFragment
            )
            .build()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        //REMOVES THE APP TITLE/ FRAGMENT TITLE FROM THE ACTION BAR
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onSupportNavigateUp(): Boolean {
        Log.i("Main", "trying to back?")
        val navController = this.findNavController(R.id.myNavHostFragment)
        return navController.navigateUp()
    }
}