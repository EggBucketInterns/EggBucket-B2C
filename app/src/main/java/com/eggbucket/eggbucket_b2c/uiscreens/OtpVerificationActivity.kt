package com.eggbucket.eggbucket_b2c.uiscreens

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.eggbucket.eggbucket_b2c.R
import com.eggbucket.eggbucket_b2c.databinding.ActivityOtpVerificationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class OtpVerificationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityOtpVerificationBinding
    private var verificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflating the binding and setting the content view
        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Request focus and start OTP view animations
        binding.otpPinView.requestFocus()
        binding.otpPinView.setAnimationEnable(true)
        binding.otpPinView.animate()

        Log.d("pinview", "start pinview")

        // Show the keyboard programmatically
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(binding.otpPinView, InputMethodManager.SHOW_IMPLICIT)
        Log.d("pinview2", "start pinview")

        // Set insets for the view to support edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance()
        verificationId = intent.getStringExtra("verificationId")

        // Add a text watcher to handle changes in the OTP input
        binding.otpPinView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("pinview4", "OTP changed: ${s?.toString()}")
                if (s?.length == 6) {
                    verifyCode(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // Method to verify the OTP code
    private fun verifyCode(code: String) {
        verificationId?.let {
            val credential = PhoneAuthProvider.getCredential(it, code)
            signInWithPhoneAuthCredential(credential)
        }
    }

    // Method to handle sign-in with OTP
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = task.result?.user
                    Toast.makeText(this, "Authentication successful", Toast.LENGTH_SHORT).show()
                    // TODO: Navigate to main activity or update UI
                } else {
                    // Sign in failed, display a message and update the UI
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}