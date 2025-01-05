package com.eggbucket.eggbucket_b2c

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.util.GeoPoint
import java.io.IOException


class AddAddressFragment : Fragment() {


    private var flatNo: String? = null
    private var addressLine1: String? = null
    private var addressLine2: String? = null
    private var area: String? = null
    private var city: String? = null
    private var state: String? = null
    private var zipCode: String? = null
    private var country: String? = null
    private var coordinates:GeoPoint? = null
    private lateinit var phoneNumber: String
    private lateinit var progressBar: ProgressBar





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_add_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar=view.findViewById(R.id.progress_bar)
        val sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        phoneNumber = sharedPreferences.getString("user_phone", "916363894956").toString()
        val savedAddressJson = sharedPreferences.getString("address", null)
        if (savedAddressJson != null) {
            // Deserialize the JSON
            val retrievedAddress = Gson().fromJson(savedAddressJson, FinalAddress::class.java)
            flatNo = retrievedAddress.fullAddress.flatNo
            addressLine1 = retrievedAddress.fullAddress.addressLine1
            addressLine2 = retrievedAddress.fullAddress.addressLine2
            area = retrievedAddress.fullAddress.area
            city = retrievedAddress.fullAddress.city
            state = retrievedAddress.fullAddress.state
            zipCode = retrievedAddress.fullAddress.zipCode
            country = retrievedAddress.fullAddress.country
            coordinates = retrievedAddress.coordinates

            // Log the retrieved data
            Log.d("SharedPreferences", "Retrieved Address JSON: $savedAddressJson")
            Log.d("SharedPreferences", "Retrieved Address Object: $retrievedAddress")
        } else {
            Log.d("SharedPreferences", "No address found in SharedPreferences")
        }
        val editor = sharedPreferences.edit()
        editor.remove("address")
        editor.apply()

        val final_flatno = view.findViewById<EditText>(R.id.et_flatno)
        val final_address1 = view.findViewById<EditText>(R.id.et_addressLine1)
        val final_address2 = view.findViewById<EditText>(R.id.et_addressLine2)
        val final_area = view.findViewById<EditText>(R.id.et_area)
        val final_city = view.findViewById<EditText>(R.id.et_city)
        val final_state = view.findViewById<EditText>(R.id.et_state)
        val final_country = view.findViewById<EditText>(R.id.et_country)
        val final_postalcode = view.findViewById<EditText>(R.id.et_postalcode)


        // Populate the fields with retrieved data if available
        final_flatno.setText(flatNo)
        final_address1.setText(addressLine1)
        final_address2.setText(addressLine2)
        final_area.setText(area)
        final_city.setText(city)
        final_state.setText(state)
        final_country.setText(country)
        final_postalcode.setText(zipCode)



        val saveButton = view.findViewById<Button>(R.id.btn_submit)
        saveButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            saveButton.isEnabled = false
            val jsonAddress = createAddressJson(
                final_flatno.text.toString(),
                final_address1.text.toString(),
                final_address2.text.toString(),
                final_area.text.toString(),
                final_city.text.toString(),
                final_state.text.toString(),
                final_postalcode.text.toString(),
                final_country.text.toString(),
                coordinates!!
            )

            patchUserAddress(phoneNumber, jsonAddress) { isSuccess ->
                progressBar.visibility = View.GONE
                saveButton.isEnabled = true

                if (isSuccess) {
                    findNavController().navigate(R.id.action_addAddressFragment_to_addressListFragment)
                } else {
                    Toast.makeText(context, "Failed to update address", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun createAddressJson(

        flatNo: String,
        addressLine1: String,
        addressLine2: String,
        area: String,
        city: String,
        state: String,
        zipCode: String,
        country: String,
        coordinates: GeoPoint
    ): String {
        // Check values before JSON creation
        Log.d("AddressInfo", "Flat No: ${flatNo}")
        Log.d("AddressInfo", "Address Line 1: ${addressLine1}")
        val addressDetails = JSONObject().apply {
            put("flatNo", flatNo)
            put("addressLine1", addressLine1)
            put("addressLine2", addressLine2)
            put("area", area)
            put("city", city)
            put("state", state)
            put("zipCode", zipCode)
            put("country", country)
        }

        val coordinatesObject = JSONObject().apply {
            put("lat", coordinates.latitude)
            put("long", coordinates.longitude)
        }

        val addressObject = JSONObject().apply {
            put("fullAddress", addressDetails)
            put("coordinates", coordinatesObject)
        }

        val addressesArray = JSONArray().apply {
            put(addressObject)
        }

        return addressesArray.toString()
    }


    // Function to patch user address using the API
    private fun patchUserAddress(userId: String, addressJson: String ,callback: (Boolean) -> Unit) {
        val client = OkHttpClient()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("addresses", addressJson)
            .build()

        val request = Request.Builder()
            .url("https://b2c-backend-1.onrender.com/api/v1/customer/user/$phoneNumber")
            .patch(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Message", Toast.LENGTH_SHORT).show()
                    callback(false)
                } // Handle error
            }


            override fun onResponse(call: Call, response: Response) {
                Handler(Looper.getMainLooper()).post {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Address updated successfully!", Toast.LENGTH_SHORT)
                            .show()
                        callback(true)
                    } else {
                        val responseBody = response.body?.string() ?: "Unknown error"
                        callback(false)
                        Toast.makeText(
                            context,
                            "Failed to update address: $responseBody",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    println("Response Code: ${response.code}")
                    println("Response Body: ${response.body?.toString()}")
                }

            }
        })
    }


}

