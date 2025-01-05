package com.eggbucket.eggbucket_b2c.BottomNavigation.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.navigation.fragment.findNavController
import com.eggbucket.eggbucket_b2c.R
import com.eggbucket.eggbucket_b2c.databinding.FragmentProfileBinding
import com.eggbucket.eggbucket_b2c.uiscreens.LoginWithOtpActivity

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        sharedPref = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userPhone = sharedPref.getString("user_phone", null)


        val name=sharedPref.getString("name",null)


        val email=sharedPref.getString("email",null)


        binding.personName.text = name
        binding.phoneNo.text = userPhone

        setupClickListeners()
        return binding.root
    }

    private fun setupClickListeners() {
        // Navigate to Address Fragment
        binding.addressesLayout.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_notifications_to_addressListFragment)
        }

        // Navigate to Order History
        binding.yourOrdersLayout.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_notifications_to_orderHistory)
        }
        binding.editProfileLayout.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_notifications_to_editProfile)
        }

        binding.logoutLayout.setOnClickListener{
                logout()
        }
    }

    private fun logout() {
        // Clear user phone number from SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()

        // Redirect to LoginActivity
        val intent = Intent(requireActivity(), LoginWithOtpActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) // Clear the stack
        startActivity(intent)

        // Optionally finish the current activity if needed
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}