package com.eggbucket.eggbucket_b2c.BottomNavigation.ui

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.eggbucket.eggbucket_b2c.R
import com.eggbucket.eggbucket_b2c.databinding.ActivityBottomNavigationScreenBinding

class BottomNavigationScreen : AppCompatActivity() {

    private lateinit var binding: ActivityBottomNavigationScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBottomNavigationScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host)

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.cartFragment, R.id.navigation_notifications)
        )

        // Attach a custom listener to handle the Home button press
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    if (navController.currentDestination?.id != R.id.navigation_home) {
                        navController.navigate(R.id.navigation_home)
                    }
                    true
                }
                R.id.navigation_dashboard -> {
                    navController.navigate(R.id.cartFragment)
                    true
                }
                R.id.navigation_notifications -> {
                    navController.navigate(R.id.navigation_notifications)
                    true
                }
                else -> false
            }
        }
    }
}
