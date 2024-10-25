package com.eggbucket.eggbucket_b2c.uiscreens
//
import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.eggbucket.eggbucket_b2c.HomeScreen
import com.eggbucket.eggbucket_b2c.R
import com.eggbucket.eggbucket_b2c.databinding.ActivityLoginWithOtpBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginWithOtpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginWithOtpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the binding
        binding = ActivityLoginWithOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginWithOtp)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Send OTP on button click
        binding.btnSendOtp.setOnClickListener {
            val phoneNumber = binding.etPhoneNumber.text.toString()
            if (phoneNumber.isNotEmpty()) {
                sendVerificationCode(phoneNumber)
            } else {
                Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {
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
            Toast.makeText(this@LoginWithOtpActivity, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            val intent = Intent(this@LoginWithOtpActivity, OtpVerificationActivity::class.java)
            intent.putExtra("verificationId", verificationId)
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
}