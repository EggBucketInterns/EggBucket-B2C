package com.eggbucket.eggbucket_b2c.uiscreens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eggbucket.eggbucket_b2c.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var etPhone: EditText
    private lateinit var btnSendOtp: Button
    private lateinit var tvCreateAccount: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_with_otp)

        etPhone = findViewById(R.id.etPhone)
        btnSendOtp = findViewById(R.id.btnSendOtp)
        tvCreateAccount = findViewById(R.id.createAccountLink) // TextView, not Button

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        btnSendOtp.setOnClickListener {
            val phone = etPhone.text.toString().trim()

            if (phone.isEmpty()) {
                Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!phone.matches(Regex("^\\d{10}\$"))) {
                Toast.makeText(this, "Enter valid 10-digit phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if user exists in Firestore
            db.collection("users")
                .whereEqualTo("phoneNumber", phone)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        // User exists → Send OTP
                        val intent = Intent(this, OtpVerificationActivity::class.java)
                        intent.putExtra("phoneNumber", phone)
                        startActivity(intent)
                    } else {
                        // Redirect to User Error page
                        val intent = Intent(this, UserErrorActivity::class.java)
                        startActivity(intent)
                    }

                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Set click listener for "Create Account" text outside btnSendOtp click listener
        tvCreateAccount.setOnClickListener {
            val phone = etPhone.text.toString().trim()
            if (phone.isNotEmpty() && !phone.matches(Regex("^\\d{10}\$"))) {
                Toast.makeText(this, "Enter valid 10-digit phone number to create account", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, CreateAccountActivity::class.java)
            if (phone.isNotEmpty()) {
                intent.putExtra("phoneNumber", phone)  // pass phone if entered
            }
            startActivity(intent)
        }
    }
}
