package com.eggbucket.eggbucket_b2c

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EditProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Get the view with ID 'main'
        val mainView = findViewById<View>(R.id.main)
        val backBtn = findViewById<ImageView>(R.id.backBtn)

        backBtn.setOnClickListener {
            // Start MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("navigateTo", "fragment_profile") // Optional: Pass data to MainActivity
            startActivity(intent)
            finish() // Close EditProfile activity
        }

        // Check if the view is not null and apply the window insets listener
        mainView?.post {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }
}
