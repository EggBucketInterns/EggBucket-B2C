package com.eggbucket.eggbucket_b2c.uiscreens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.eggbucket.eggbucket_b2c.R

class UserErrorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_error)

        val btnCreateAccount = findViewById<Button>(R.id.sendOtpButton)

        btnCreateAccount.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
