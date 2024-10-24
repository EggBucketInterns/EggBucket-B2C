package com.eggbucket.eggbucket_b2c

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class AddAddressFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_add_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//         Set up any logic or listeners here
//        btn_submit.setOnClickListener {
//            // Handle the button click here
//            val name = et_name.text.toString()
//            val address = et_address1.text.toString()
//            val city = et_city.text.toString()
//            val state = et_state.text.toString()
//            val postalCode = et_postal_code.text.toString()
//            val flatNo = flat_no.text.toString()
//            val landmark = landmark.text.toString()
//
//             Process or save the address details
//        }
   }
}
