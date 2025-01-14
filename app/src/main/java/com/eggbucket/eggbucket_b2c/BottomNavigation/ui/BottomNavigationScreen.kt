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
        makeApiRequestWithRetries2()
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
    private fun makeApiRequestWithRetries() {
        CoroutineScope(Dispatchers.IO).launch {
            val url = "https://b2c-backend-1.onrender.com/api/v1/customer/user/916363894956"
            var attempts = 0
            var success = false

            while (attempts < 2 && !success) {
                try {
                    val connection = URL(url).openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"

                    val responseCode = connection.responseCode
                    if (responseCode == 404) {
                        success = true
                        val response = connection.inputStream.bufferedReader().use { it.readText() }
                        Log.d("API_RESPONSE", response)
                    } else {
                        Log.e("API_ERROR", "Response code: $responseCode")
                    }
                } catch (e: Exception) {
                    Log.e("API_Refresh", "Exception: ${e.message}")
                } finally {
                    attempts++
                }
            }

            if (!success) {
                Log.e("API_ERROR", "API request failed after 3 attempts.")
            }
        }
    }
    private fun makeApiRequestWithRetries2() {
        CoroutineScope(Dispatchers.IO).launch {
            val url = "https://b2c-backend-1.onrender.com/api/v1/order/order"
            var attempts = 0
            var success = false

            // Define the default body as a JSON string
            val requestBody = """
            {
                "address": {
                    "fullAddress": {
                        "flatNo": "0",
                        "area": "0",
                        "city": "0",
                        "state": "0",
                        "zipCode": "0",
                        "country": "0",
                        "addressLine1": "0",
                        "addressLine2": "0"
                    }
                },
                "amount": 0,
                "products": {
                    "E6": 0,
                    "E12": 0,
                    "E30": 0
                },
                "customerId": "00000000"
            }
        """.trimIndent()

            while (attempts < 2 && !success) {
                try {
                    val connection = URL(url).openConnection() as HttpURLConnection
                    connection.requestMethod = "POST"
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.doOutput = true

                    // Write the request body to the output stream
                    connection.outputStream.use { outputStream ->
                        outputStream.write(requestBody.toByteArray(Charsets.UTF_8))
                    }

                    val responseCode = connection.responseCode
                    if (responseCode == 200 || responseCode == 404) { // Handle success codes
                        success = true
                        val response = connection.inputStream.bufferedReader().use { it.readText() }
                        Log.d("API_RESPONSE", response)
                    } else {
                        Log.e("API_ERROR2", "Response code: $responseCode")
                        Log.e("API_ERROR2", "Response body:${connection.responseMessage} ")
                    }
                } catch (e: Exception) {
                    Log.e("API_ERROR2", "Exception: ${e.message}")

                } finally {
                    attempts++
                }
            }

            if (!success) {
                Log.e("API_ERROR", "API request failed after 2 attempts.")
            }
        }
    }

}
