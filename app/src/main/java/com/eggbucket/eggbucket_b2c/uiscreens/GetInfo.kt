package com.eggbucket.eggbucket_b2c.uiscreens


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.eggbucket.eggbucket_b2c.BottomNavigation.ui.BottomNavigationScreen
import com.eggbucket.eggbucket_b2c.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class GetInfo : AppCompatActivity() {

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var genderSpinner: Spinner
    private lateinit var updateProfileButton: AppCompatButton
    private lateinit var phoneno:String
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_info)

        // Initialize views
        firstNameEditText = findViewById(R.id.first_name)
        lastNameEditText = findViewById(R.id.last_name)
        cityEditText = findViewById(R.id.city)
        emailEditText = findViewById(R.id.edt_email)
        ageEditText = findViewById(R.id.age_input)
        genderSpinner = findViewById(R.id.gender_spinner)
        updateProfileButton = findViewById(R.id.update_profile_btn)
        sharedPreferences=getSharedPreferences("MyPreferences", MODE_PRIVATE)
        phoneno=sharedPreferences.getString("user_phone","")!!


        // Set up gender spinner with options
        val genderOptions = arrayOf("select","Male", "Female", "Other")
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = genderAdapter

        // Handle button click to submit form
        updateProfileButton.setOnClickListener {
            submitForm()
        }
    }

    private fun submitForm() {
        val firstName = firstNameEditText.text.toString()
        val lastName = lastNameEditText.text.toString()
        val city = cityEditText.text.toString()
        val email = emailEditText.text.toString()
        val age = ageEditText.text.toString()
        val gender = genderSpinner.selectedItem.toString()


        // Simple validation
        if (firstName.isEmpty() || lastName.isEmpty() || city.isEmpty() || email.isEmpty() || age.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()


        } else {
            createUser(firstName,lastName,city,email,age,gender)



        }
    }


    fun createUser(firstName:String,lastName:String,city:String,email:String,age:String,gender:String) {
        val client = OkHttpClient()

        // Construct the JSON body
        val jsonBody = JSONObject().apply {

            put("phone", phoneno)// Phone is required
            put("name","${firstName}  ${lastName}")

            put("email", email)
            put("age", age)
            put("gender", gender)
            put("city",city)
            put("password","1234567")

        }
        Log.d("API CALL",jsonBody.toString())

        // Create the request body
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            jsonBody.toString()
        )

        // Construct the request
        val request = Request.Builder()
            .url("https://b2c-backend-1.onrender.com/api/v1/customer/user")  // Replace {{host}} with the actual host
            .post(body)
            .build()

        // Make the request
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // Handle failure
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {

                   Log.d("response",response.toString())
                    val editor = sharedPreferences.edit()
                    editor.putString("name", "${firstName}  ${lastName}")
                    editor.putString("user_phone", phoneno)
                    editor.putString("email", email)
                    editor.apply()
                    intent= Intent(this@GetInfo,BottomNavigationScreen::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.d("response",response.toString())
                    // Handle unsuccessful response

                }
            }
        })
    }
}
