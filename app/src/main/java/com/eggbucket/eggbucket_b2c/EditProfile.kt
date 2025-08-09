package com.eggbucket.eggbucket_b2c

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class EditProfileFragment : Fragment() {

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var emailEditText: EditText
    private lateinit var updateProfileButton: Button
    private lateinit var goBackImageView: ImageView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.edit_profile, container, false)

        firstNameEditText = view.findViewById(R.id.first_name)
        lastNameEditText = view.findViewById(R.id.last_name)
        emailEditText = view.findViewById(R.id.edt_email)
        updateProfileButton = view.findViewById(R.id.update_profile_btn)
        goBackImageView = view.findViewById(R.id.goBack)
        sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        progressBar=view.findViewById(R.id.Edit_profile_progressbar)
        loadProfileData()

        goBackImageView.setOnClickListener {
            findNavController().navigate(R.id.action_editProfile_to_navigation_notifications)
        }

        val mainView = view.findViewById<View>(R.id.main)
        mainView?.post {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        updateProfileButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val email = emailEditText.text.toString()

            if (firstName.isBlank() || lastName.isBlank() || email.isBlank()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                saveProfileData(firstName, lastName, email)
                sendProfileDataToServer("$firstName $lastName", email)
            }
        }

        return view
    }

    private fun saveProfileData(firstName: String, lastName: String,email: String) {
        val editor = sharedPreferences.edit()
        editor.putString("firstName", firstName)
        editor.putString("lastName", lastName)
        editor.putString("name","$firstName $lastName")
        editor.putString("email", email)
        editor.apply()
    }

    private fun loadProfileData() {
        firstNameEditText.setText(sharedPreferences.getString("firstName", ""))
        lastNameEditText.setText(sharedPreferences.getString("lastName", ""))
        emailEditText.setText(sharedPreferences.getString("email", ""))
    }

    private fun sendProfileDataToServer(name: String, email: String) {
        progressBar.visibility=View.VISIBLE
        val phone = sharedPreferences.getString("user_phone", "9999999999")
        val url = "https://b2c-backend-eik4.onrender.com/api/v1/customer/user/$phone"
        val client = OkHttpClient()

        val jsonBody = JSONObject().apply {
            put("name", name)
            put("email", email)
        }
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            jsonBody.toString()
        )

        val request = Request.Builder()
            .url(url)
            .patch(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    progressBar.visibility=View.INVISIBLE
                    Toast.makeText(requireContext(), "Failed to update profile on server", Toast.LENGTH_SHORT).show()
                    Log.e("EditProfileFragment", "Error: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    if (response.isSuccessful) {
                        progressBar.visibility=View.INVISIBLE
                        Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_editProfile_to_navigation_notifications)
                    } else {
                        progressBar.visibility=View.INVISIBLE
                        Log.e("EditProfileFragment", "Response Code: ${response.code}")
                    }
                }
            }
        })
    }
}
