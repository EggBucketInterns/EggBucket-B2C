package com.eggbucket.eggbucket_b2c

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditAddressFragment : Fragment() {
    private lateinit var etFlatNo: EditText
    private lateinit var etLine1: EditText
    private lateinit var etLine2: EditText
    private lateinit var etArea: EditText
    private lateinit var etCity: EditText
    private lateinit var etState: EditText
    private lateinit var etPostalCode: EditText
    private lateinit var etCountry: EditText
    private lateinit var submitBtn: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var phoneNumber: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userAddress: UserAddress
    private var addressPosition: Int = -1 // To store the position of the address

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_edit_address, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize views
        etFlatNo = view.findViewById(R.id.et_flatno)
        etLine1 = view.findViewById(R.id.et_addressLine1)
        etLine2 = view.findViewById(R.id.et_addressLine2)
        etArea = view.findViewById(R.id.et_area)
        etCity = view.findViewById(R.id.et_city)
        etState = view.findViewById(R.id.et_state)
        etPostalCode = view.findViewById(R.id.et_postalcode)
        etCountry = view.findViewById(R.id.et_country)
        submitBtn = view.findViewById(R.id.btn_submit)
        progressBar = view.findViewById(R.id.progress_bar)

        // Set which fields are editable
        etFlatNo.isEnabled = true
        etLine1.isEnabled = true
        etLine2.isEnabled = false
        etArea.isEnabled = false
        etCity.isEnabled = false
        etState.isEnabled = false
        etPostalCode.isEnabled = false
        etCountry.isEnabled = false

        sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        phoneNumber = sharedPreferences.getString("user_phone", "") ?: ""

        // Get both the address data and its position from the bundle
        val addressJson = arguments?.getString("address_json")
        addressPosition = arguments?.getInt("address_position", -1) ?: -1 // Get the position

        if (addressJson != null && addressPosition != -1) {
            userAddress = Gson().fromJson(addressJson, UserAddress::class.java)
            populateFields()
        } else {
            Toast.makeText(requireContext(), "Error: Address data or position not found.", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
        }

        submitBtn.setOnClickListener {
            updateAddress()
        }
    }

    private fun populateFields() {
        val fullAddress = userAddress.fullAddress
        etFlatNo.setText(fullAddress.flatNo ?: "")
        etLine1.setText(fullAddress.addressLine1 ?: "")
        etLine2.setText(fullAddress.addressLine2 ?: "")
        etArea.setText(fullAddress.area ?: "")
        etCity.setText(fullAddress.city ?: "")
        etState.setText(fullAddress.state ?: "")
        etPostalCode.setText(fullAddress.zipCode ?: "")
        etCountry.setText(fullAddress.country ?: "")
    }

    private fun updateAddress() {
        if (etFlatNo.text.toString().isBlank() || etLine1.text.toString().isBlank()) {
            Toast.makeText(requireContext(), "Please fill all editable fields.", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE
        submitBtn.isEnabled = false

        // 1. Create the updated FullAddress object from the user's input
        val updatedFullAddress = userAddress.fullAddress.copy(
            flatNo = etFlatNo.text.toString(),
            addressLine1 = etLine1.text.toString()
        )

        // 2. Construct the request body using the POSITION and the updated data
        val updateRequest = UpdateAddressByIndexRequest(
            index = addressPosition,
            fullAddress = updatedFullAddress
        )

        // 3. Make the API call to the endpoint that updates by index
        RetrofitClient.apiService.updateUserAddressByIndex(phoneNumber, updateRequest)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    progressBar.visibility = View.GONE
                    submitBtn.isEnabled = true
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Address updated successfully", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Update failed: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    submitBtn.isEnabled = true
                    Toast.makeText(requireContext(), "An error occurred: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
}