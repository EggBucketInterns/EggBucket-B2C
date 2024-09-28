package com.eggbucket.eggbucket_b2c.uiscreens

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.eggbucket.eggbucket_b2c.R
import com.eggbucket.eggbucket_b2c.databinding.ActivityLoginWithOtpBinding

class LoginWithOtpActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginWithOtpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityLoginWithOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtLoginWithEmail.apply { this.setBackgroundResource(R.drawable.email_back) }
        binding.txtLoginWithEmail.apply {
            this.setOnClickListener {  this.setBackgroundResource(R.drawable.email_back)
                binding.txtLoginWithPhoneNumber.background = null
            }
        }
        binding.txtLoginWithPhoneNumber.apply {
            this.setOnClickListener {
                this.setBackgroundResource(R.drawable.phone_number_back)
                binding.txtLoginWithEmail.background = null
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}