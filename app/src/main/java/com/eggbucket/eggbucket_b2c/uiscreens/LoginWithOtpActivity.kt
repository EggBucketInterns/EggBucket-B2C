package com.eggbucket.eggbucket_b2c.uiscreens
//
import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.eggbucket.eggbucket_b2c.R
import com.eggbucket.eggbucket_b2c.databinding.ActivityLoginWithOtpBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

class LoginWithOtpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginWithOtpBinding
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the binding
        binding = ActivityLoginWithOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressBar=binding.loginprogressBar

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginWithOtp)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        makeApiRequestWithRetries01()
        // Send OTP on button click
        binding.btnSendOtp.setOnClickListener {
            val phoneNumber = binding.etPhoneNumber.text.toString()
            if (phoneNumber.isNotEmpty()) {
                sendVerificationCode("+91${phoneNumber}")
            } else {
                Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        progressBar.visibility=VISIBLE
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)

            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // Callback for phone auth verification
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            progressBar.visibility=INVISIBLE
            Toast.makeText(this@LoginWithOtpActivity, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            val phoneNumber = binding.etPhoneNumber.text.toString()
            progressBar.visibility=INVISIBLE
            val intent = Intent(this@LoginWithOtpActivity, OtpVerificationActivity::class.java)
            intent.putExtra("verificationId", verificationId)
            intent.putExtra("phoneNumber", phoneNumber)
            startActivity(intent)
        }
    }



    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, handle UI update here
                    // TODO: Navigate to main activity or update UI
                    Toast.makeText(this, "Authentication successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
    // this function is to juat make api active for the next uses
    private fun makeApiRequestWithRetries01() {
        CoroutineScope(Dispatchers.IO).launch {
            val url = "https://b2c-backend-1.onrender.com/api/v1/order/order/000000000"
            var attempts = 0
            var success = false

            while (attempts < 3 && !success) {
                try {
                    val connection = URL(url).openConnection() as HttpURLConnection
                    connection.requestMethod = "POST"

                    val responseCode = connection.responseCode
                    if (responseCode == 200 ||responseCode == 404) {
                        success = true
                        val response = connection.inputStream.bufferedReader().use { it.readText() }
                        Log.d("API_RESPONSE", response)
                    } else {
                        Log.e("API_ERROR", "Response code: $responseCode")
                    }
                } catch (e: Exception) {
                    Log.e("API_ERROR", "Exception: ${e.message}")
                } finally {
                    attempts++
                }
            }

            if (!success) {
                Log.e("API_ERROR", "API request failed after 3 attempts.")
            }
        }
    }

}