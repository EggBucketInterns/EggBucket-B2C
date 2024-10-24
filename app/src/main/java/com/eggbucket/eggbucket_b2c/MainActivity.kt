package com.eggbucket.eggbucket_b2c

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navigateTo = intent.getStringExtra("navigateTo")
        if (navigateTo == "fragment_profile") {
            findNavController(R.id.nav_host).navigate(R.id.navigation_notifications) // Replace with your actual fragment class
        }
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.homeFragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
