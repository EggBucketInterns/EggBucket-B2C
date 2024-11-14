package com.eggbucket.eggbucket_b2c.uiscreens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.eggbucket.eggbucket_b2c.BottomNavigation.ui.BottomNavigationScreen
import com.eggbucket.eggbucket_b2c.R
import com.eggbucket.eggbucket_b2c.databinding.ActivityOtpVerificationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class OtpVerificationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityOtpVerificationBinding
    private var verificationId: String? = null
    private var phoneNumber: String? = null
    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        startCountdownTimer()

        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.otpPinView.requestFocus()
        binding.otpPinView.setAnimationEnable(true)
        binding.otpPinView.animate()

        val backArrow = findViewById<ImageView>(R.id.imageView4)
        backArrow.setOnClickListener {
            val intent = Intent(this, LoginWithOtpActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish() // Optional: Closes the current activity so it's removed from the back stack
        }

        Log.d("pinview", "start pinview")

        // Show the keyboard programmatically
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        Log.d("pinview2", "start pinview")

        // Set up text watcher for OTP input
        binding.otpPinView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // You can log the previous input or do any necessary checks here
                Log.d("pinview", "Before text changed: $s")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("pinview4", "On text changed: $s")
                binding.verifyButton.isEnabled = s?.length == 6
                if (s.toString().length == 6) {
                    Toast.makeText(this@OtpVerificationActivity, "It's Working", Toast.LENGTH_SHORT).show()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 6) {
                    verifyCode(s.toString())
                }
            }
        })

        // Set insets for the view to support edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        verificationId = intent.getStringExtra("verificationId")
        phoneNumber = intent.getStringExtra("phoneNumber")
        binding.textView4.text = "Enter the code from the SMS we sent to " + phoneNumber
    }

    private fun verifyCode(code: String) {
        verificationId?.let {
            val credential = PhoneAuthProvider.getCredential(it, code)
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun startCountdownTimer() {
        timer = object : CountDownTimer(150000, 1000) { // 2 min 30 sec in milliseconds
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                val time = String.format("%02d:%02d", minutes, seconds)
                binding.textView5.text = time
            }

            override fun onFinish() {
                binding.textView5.text = "00:00"
                // Handle any actions after the timer finishes here, such as disabling input or resending OTP
            }
        }
        timer.start()
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user

                    val sharedPref = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    val sanitizedPhoneNumber = user?.phoneNumber?.replace("+", "")
                    editor.putString("user_id", user?.uid)
                    editor.putString("user_phone", sanitizedPhoneNumber)
                    editor.apply()

                    Toast.makeText(this, "Authentication successful", Toast.LENGTH_SHORT).show()

                    // Hide OTP verification UI if applicable

                    // Start BottomNavigationActivity
                    val intent = Intent(this, BottomNavigationScreen::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) // Clear the stack
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

}
