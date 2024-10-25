package com.eggbucket.eggbucket_b2c

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.eggbucket.eggbucket_b2c.BottomNavigation.ui.BottomNavigationScreen
import com.eggbucket.eggbucket_b2c.uiscreens.LoginWithOtpActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if the user phone number exists in shared preferences
        val sharedPref: SharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val userPhone = sharedPref.getString("user_phone", null)

        if (userPhone != null) {
            // User already exists, navigate to BottomNavigationScreen
            val intent = Intent(this, BottomNavigationScreen::class.java)
            startActivity(intent)
        } else {
            // User does not exist, navigate to LoginWithOtpActivity
            val intent = Intent(this, LoginWithOtpActivity::class.java)
            startActivity(intent)
        }

        // Close MainActivity so that the user cannot go back to it
        finish()
    }

    // Existing fragment navigation method (optional, keep it if you need it)
    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.homeFragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
