package com.eggbucket.eggbucket_b2c

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.eggbucket.eggbucket_b2c.R

class EditProfileFragment : Fragment() {

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var updateProfileButton: Button
    private lateinit var goBackImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.edit_profile, container, false)

        // Initialize views
        firstNameEditText = view.findViewById(R.id.first_name)
        lastNameEditText = view.findViewById(R.id.last_name)
        phoneNumberEditText = view.findViewById(R.id.phone_number)
        emailEditText = view.findViewById(R.id.edt_email)
        updateProfileButton = view.findViewById(R.id.update_profile_btn)
        goBackImageView = view.findViewById(R.id.goBack)

        // Set up click listener for "Go Back" button
        goBackImageView.setOnClickListener {
            findNavController().navigate(R.id.action_editProfile_to_navigation_notifications)
        }

        // Apply padding for system bars (optional)
        val mainView = view.findViewById<View>(R.id.main)
        mainView?.post {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // Set up click listener for "Update Profile" button
        updateProfileButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val phoneNumber = phoneNumberEditText.text.toString()
            val email = emailEditText.text.toString()

            if (firstName.isBlank() || lastName.isBlank() || phoneNumber.isBlank() || email.isBlank()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Pass data to ProfileFragment
                val bundle = Bundle().apply {
                    putString("firstName", firstName)
                    putString("lastName", lastName)
                    putString("phoneNumber", phoneNumber)
                    putString("email", email)
                }

                // Navigate to ProfileFragment
                findNavController().navigate(R.id.action_editProfile_to_navigation_notifications, bundle)
            }
        }

        return view
    }
}
