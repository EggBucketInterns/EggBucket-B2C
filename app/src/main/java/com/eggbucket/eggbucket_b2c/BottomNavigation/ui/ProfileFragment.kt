package com.eggbucket.eggbucket_b2c.BottomNavigation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.eggbucket.eggbucket_b2c.R
import com.eggbucket.eggbucket_b2c.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val firstName = arguments?.getString("firstName")
        val lastName = arguments?.getString("lastName")
        val phoneNumber = arguments?.getString("phoneNumber")
        val email = arguments?.getString("email")
        println("$firstName, $lastName, $phoneNumber, $email")


        binding.personName.text = "$firstName $lastName"
        binding.phoneNo.text = phoneNumber

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
        binding.editProfileBtn.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_notifications_to_editProfile)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}