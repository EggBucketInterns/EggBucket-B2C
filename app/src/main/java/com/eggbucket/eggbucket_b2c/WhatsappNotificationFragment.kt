package com.eggbucket.eggbucket_b2c

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class WhatsappNotificationFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_whatsapp_noti, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val backbutn=view.findViewById<ImageView>(R.id.backBtn)
        val switch=view.findViewById<Switch>(R.id.switch1)
        val sharedPref = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val isSwitchChecked = sharedPref.getBoolean("whatsapp_switch_state", false)
        switch.isChecked = isSwitchChecked
        backbutn.setOnClickListener {
            findNavController().popBackStack()
        }

        switch.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("whatsapp_switch_state", isChecked).apply()
            if (isChecked) {
                Toast.makeText(requireContext(),"Whatsapp Notifications Enabled",Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(),"Whatsapp Notifications Disabled",Toast.LENGTH_SHORT).show()
            }
        }

    }
}