package com.eggbucket.eggbucket_b2c.uiscreens


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.Navigation.findNavController
import com.eggbucket.eggbucket_b2c.BottomNavigation.ui.BottomNavigationScreen
import com.eggbucket.eggbucket_b2c.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class GetInfo : AppCompatActivity() {

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var updateProfileButton: AppCompatButton
    private lateinit var phoneno: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_info)

        // Initialize views
        firstNameEditText = findViewById(R.id.first_name)
        lastNameEditText = findViewById(R.id.last_name)
        updateProfileButton = findViewById(R.id.update_profile_btn)
        progressBar = findViewById(R.id.progressBar3)
        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)

        phoneno = sharedPreferences.getString("user_phone", "") ?: ""

        // Handle button click to submit form
        updateProfileButton.setOnClickListener {
            submitForm()
        }
    }

    private fun submitForm() {
        val firstName = firstNameEditText.text.toString()
        val lastName = lastNameEditText.text.toString()

        // Simple validation
        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        } else {
            createUser(firstName, lastName)
        }
    }

    private fun createUser(firstName: String, lastName: String) {
        val fullName = "$firstName $lastName"
        progressBar.visibility = VISIBLE
        val client = OkHttpClient()

        // Construct the JSON body
        val jsonBody = JSONObject().apply {
            put("name", fullName)
        }

        Log.d("API CALL", jsonBody.toString())

        // Create the request body
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            jsonBody.toString()
        )

        // Construct the request
        val request = Request.Builder()
            .url("https://b2c-backend-eik4.onrender.com/api/v1/customer/user/$phoneno")
            .patch(body)
            .build()

        // Make the request
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    progressBar.visibility = GONE
                    Toast.makeText(this@GetInfo, "Failed to update profile. Please try again.", Toast.LENGTH_SHORT).show()
                }
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    progressBar.visibility = GONE
                    if (response.isSuccessful) {
                        Log.d("API SUCCESS", response.body?.string().orEmpty())
                        val editor = sharedPreferences.edit()
                        editor.putString("name", fullName)
                        editor.putString("user_phone", phoneno)
                        editor.apply()

                        Toast.makeText(this@GetInfo, "Profile updated! Continue shopping.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@GetInfo, BottomNavigationScreen::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.e("API ERROR", "Failed to update user: ${response.code}")
                        Toast.makeText(this@GetInfo, "Failed to update profile. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
