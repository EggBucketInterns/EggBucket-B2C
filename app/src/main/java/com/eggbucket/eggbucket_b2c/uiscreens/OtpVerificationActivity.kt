package com.eggbucket.eggbucket_b2c.uiscreens

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.eggbucket.eggbucket_b2c.R
import com.eggbucket.eggbucket_b2c.databinding.ActivityOtpVerificationBinding

class OtpVerificationActivity : AppCompatActivity() {
    lateinit var binding: ActivityOtpVerificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.otpPinView.requestFocus()
        binding.otpPinView.setAnimationEnable(true) // start animation when adding text
        binding.otpPinView.animation
        binding.otpPinView.animate()
        Log.d("pinview","start pinview")
        val inputMethodManager= getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        Log.d("pinview2","start pinview")
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY)
        Log.d("pinview3","start pinview")
binding.otpPinView.addTextChangedListener( object :TextWatcher{
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        Log.d("pinview4","start pinview")
       if (p0.toString().length==6){
           Toast.makeText(this@OtpVerificationActivity,"It's Working",Toast.LENGTH_SHORT).show()
       }
        Log.d("pinview5","start pinview")
    }

    override fun afterTextChanged(p0: Editable?) {
    }


})
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}