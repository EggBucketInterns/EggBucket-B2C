package com.eggbucket.eggbucket_b2c

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController

class OrderCompleted : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for the fragment
        return inflater.inflate(R.layout.fragment_order_completed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Now the view is safely available for interaction
//        val gotoHomeButton: Button = view.findViewById(R.id.gotoHome)
//        gotoHomeButton.setOnClickListener {
//            // Navigate to the home screen when the button is clicked
//            it.findNavController().navigate(R.id.action_orderCompleted_to_navigation_home)
//        }

        val viewOrderDetailsButton: Button = view.findViewById(R.id.viewOrderDetailsButton)
        viewOrderDetailsButton.setOnClickListener {
            // Create an Intent to start ViewOrderActivity
            val intent = Intent(requireContext(), ViewOrderActivity::class.java)
            startActivity(intent)
        }


    }

    companion object {
        @JvmStatic
        fun newInstance() = OrderCompleted()
    }
}
