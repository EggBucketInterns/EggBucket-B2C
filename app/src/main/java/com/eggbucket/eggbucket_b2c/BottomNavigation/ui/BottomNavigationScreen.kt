package com.eggbucket.eggbucket_b2c.BottomNavigation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
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

        // Setup the AppBarConfiguration
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.cartFragment, R.id.navigation_profile)
        )

        // Setup Bottom Navigation and link it with the NavController
        NavigationUI.setupWithNavController(navView, navController)

        // Attach a custom listener to handle the Home, Cart, and Notifications button press
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Prevent navigating to the same fragment (Home)
                    if (navController.currentDestination?.id != R.id.navigation_home) {
                        navController.navigate(R.id.navigation_home)
                    }
                    true
                }
                R.id.navigation_cart -> {
                    // Prevent navigating to the same fragment (Cart)
                    if (navController.currentDestination?.id != R.id.cartFragment) {
                        navController.navigate(R.id.cartFragment)
                    }
                    true
                }
                R.id.navigation_profile -> {
                    // Prevent navigating to the same fragment (Notifications)
                    if (navController.currentDestination?.id != R.id.navigation_profile) {
                        navController.navigate(R.id.navigation_profile)
                    }
                    true
                }
                else -> false
            }
        }

        // Ensure the selected item is updated when navigating back
        navController.addOnDestinationChangedListener { _, destination, _ ->
            navView.menu.findItem(destination.id)?.isChecked = true
        }

        // Handling the back button press to update the selected item
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home -> navView.selectedItemId = R.id.navigation_home
                R.id.cartFragment -> navView.selectedItemId = R.id.navigation_cart
                R.id.navigation_profile -> navView.selectedItemId = R.id.navigation_profile
            }
        }
    }
}
