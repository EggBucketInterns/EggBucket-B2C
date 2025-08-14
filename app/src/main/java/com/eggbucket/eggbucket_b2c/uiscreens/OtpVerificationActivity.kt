package com.eggbucket.eggbucket_b2c.uiscreens

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eggbucket.eggbucket_b2c.BottomNavigation.ui.BottomNavigationScreen
import com.eggbucket.eggbucket_b2c.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.chaos.view.PinView
import java.util.concurrent.TimeUnit

class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var etOtp: PinView
    private lateinit var btnVerifyOtp: TextView
    private lateinit var timerTextView: TextView
    private lateinit var resendTextView: TextView
    private lateinit var tvInstructions: TextView

    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null
    private var phoneNumber: String? = null

    private var countDownTimer: CountDownTimer? = null
    private val timerDurationMillis = 152000L // 2 minutes 32 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        // Initialize views
        etOtp = findViewById(R.id.otp_pinView)
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp)
        tvInstructions = findViewById(R.id.textView4)
        timerTextView = findViewById(R.id.textView5)
        resendTextView = findViewById(R.id.textView7)

        auth = FirebaseAuth.getInstance()

        // Back arrow click handler
        val backArrow = findViewById<ImageView>(R.id.imageView4)
        backArrow.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        phoneNumber = intent.getStringExtra("phoneNumber")
        phoneNumber?.let {
            tvInstructions.text = "Enter the code from the SMS we sent \nto $it"
            sendOtp(it)
            startCountdownTimer(timerDurationMillis)
        }

        // Initially disable resend button
        resendTextView.isEnabled = false
        resendTextView.alpha = 0.5f

        // Verify OTP button click
        btnVerifyOtp.setOnClickListener {
            val otp = etOtp.text.toString().trim()
            if (otp.isEmpty()) {
                Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            verificationId?.let {
                val credential = PhoneAuthProvider.getCredential(it, otp)
                signInWithPhoneAuthCredential(credential)
            }
        }

        // Resend OTP click
        resendTextView.setOnClickListener {
            if (resendTextView.isEnabled) {
                phoneNumber?.let {
                    sendOtp(it)
                    startCountdownTimer(timerDurationMillis)
                    resendTextView.isEnabled = false
                    resendTextView.alpha = 0.5f
                }
            }
        }
    }

    private fun startCountdownTimer(millisInFuture: Long) {
        countDownTimer?.cancel() // Cancel if any existing timer

        countDownTimer = object : CountDownTimer(millisInFuture, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000) % 60
                val minutes = (millisUntilFinished / 1000) / 60
                timerTextView.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                timerTextView.text = "00:00"
                resendTextView.isEnabled = true
                resendTextView.alpha = 1.0f
            }
        }.start()
    }

    private fun sendOtp(phone: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phone")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(this@OtpVerificationActivity, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    this@OtpVerificationActivity.verificationId = verificationId
                    Toast.makeText(this@OtpVerificationActivity, "OTP Sent", Toast.LENGTH_SHORT).show()
                }
            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, BottomNavigationScreen::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Verification Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
