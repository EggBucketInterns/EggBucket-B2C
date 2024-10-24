package com.eggbucket.eggbucket_b2c

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.fragment.findNavController

class CompanyMenu : Fragment() {
    // Parameters (if needed)
    private var param1: String? = null
    private var param2: String? = null

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_company_menu, container, false)

        // Back arrow ImageView
        val backArrow: ImageView = view.findViewById(R.id.backArrow)

        // Set up the back navigation to go to the previous fragment or activity
        backArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        val aboutUsButton = view.findViewById<Button>(R.id.aboutUsButton)
        aboutUsButton.setOnClickListener {
            findNavController().navigate(R.id.action_companyMenu_to_aboutUs)
        }
        val privacyPolicyButton = view.findViewById<Button>(R.id.privacyPolicyButton)
        privacyPolicyButton.setOnClickListener {
            findNavController().navigate(R.id.action_companyMenu_to_privacyPolicy)
        }

//        val profileButton = view.findViewById<Button>(R.id.subscriptionServicesButton)
//        profileButton.setOnClickListener {
//            findNavController().navigate(R.id.action_companyMenu_to_profile)
//        }

        return view
    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment CompanyMenu.
//         */
////        @JvmStatic
////        fun newInstance(param1: String, param2: String) =
////            CompanyMenu().apply {
////                arguments = Bundle().apply {
////                    putString(ARG_PARAM1, param1)
////                    putString(ARG_PARAM2, param2)
////                }
////            }
//    }
companion object {
    @JvmStatic
    fun newInstance() = CompanyMenu()
}

}
