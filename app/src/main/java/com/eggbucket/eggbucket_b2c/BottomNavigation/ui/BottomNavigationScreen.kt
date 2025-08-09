package com.eggbucket.eggbucket_b2c.BottomNavigation.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.eggbucket.eggbucket_b2c.R
import com.eggbucket.eggbucket_b2c.databinding.ActivityBottomNavigationScreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

class BottomNavigationScreen : AppCompatActivity() {

    private lateinit var binding: ActivityBottomNavigationScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBottomNavigationScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host)

        makeApiRequestWithRetries()

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.cartFragment, R.id.navigation_profile)
        )

        NavigationUI.setupWithNavController(navView, navController)

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    if (navController.currentDestination?.id != R.id.navigation_home) {
                        navController.navigate(R.id.navigation_home)
                    }
                    true
                }
                R.id.navigation_cart -> {
                    if (navController.currentDestination?.id != R.id.cartFragment) {
                        navController.navigate(R.id.cartFragment)
                    }
                    true
                }
                R.id.navigation_profile -> {
                    if (navController.currentDestination?.id != R.id.navigation_profile) {
                        navController.navigate(R.id.navigation_profile)
                    }
                    true
                }
                else -> false
            }
        }

        // Ensure BottomNav highlights the correct tab on navigation
        navController.addOnDestinationChangedListener { _, destination, _ ->
            navView.menu.findItem(destination.id)?.isChecked = true
        }
    }

    private fun makeApiRequestWithRetries() {
        Log.d("API Request", "Started.")

        CoroutineScope(Dispatchers.IO).launch {
            val url = "https://b2c-backend-eik4.onrender.com/api/v1/customer/user/6363894956"
            var attempts = 0
            var success = false

            while (attempts < 3 && !success) { // 3 attempts
                try {
                    val connection = URL(url).openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connectTimeout = 5000
                    connection.readTimeout = 5000

                    val responseCode = connection.responseCode
                    Log.d("API_RESPONSE", "Attempt ${attempts + 1}: Response Code - $responseCode")

                    if (responseCode == 200 || responseCode == 201) { // Only success for 200/201
                        val response = connection.inputStream.bufferedReader().use { it.readText() }
                        Log.d("API_RESPONSE", "Success: $response")
                        success = true
                    } else {
                        Log.e("API_ERROR", "Attempt ${attempts + 1}: Failed with response code $responseCode")
                    }

                    connection.disconnect()
                } catch (e: Exception) {
                    Log.e("API_ERROR", "Exception on attempt ${attempts + 1}: ${e.message}")
                }

                attempts++
                if (!success) {
                    Log.d("API_ERROR", "Retrying in 2 seconds...")
                    delay(2000)
                }
            }

            if (!success) {
                Log.e("API_ERROR", "API request failed after 3 attempts.")
            }
        }
    }
}
