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
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
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
            // Show progress bar and disable the button
            progressBar.visibility = View.VISIBLE
            saveButton.isEnabled = false

            // Validate mandatory fields
            val flatno = final_flatno.text.toString()
            val address1 = final_address1.text.toString()
            val address2 = final_address2.text.toString()
            val area = final_area.text.toString()
            val city = final_city.text.toString()
            val state = final_state.text.toString()
            val postalcode = final_postalcode.text.toString()
            val country = final_country.text.toString()

            // Check if any of the fields are empty
            if (flatno.isEmpty() || address1.isEmpty() || address2.isEmpty() || area.isEmpty() ||
                city.isEmpty() || state.isEmpty() || postalcode.isEmpty() || country.isEmpty()) {

                // Hide progress bar and enable the button again
                progressBar.visibility = View.GONE
                saveButton.isEnabled = true

                // Show a toast message to inform the user
                Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else {
                // If all fields are filled, proceed with creating the JSON and API call
                val jsonAddress = createAddressJson(
                    flatno, area, city, state, postalcode, country, coordinates!!
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

    }

    fun createAddressJson(
        flatNo: String,
        area: String,
        city: String,
        state: String,
        zipCode: String,
        country: String,
        coordinates: GeoPoint
    ): String {
        val addressDetails = JSONObject(
            mapOf(
                "flatNo" to flatNo,
                "area" to area,
                "city" to city,
                "state" to state,
                "zipCode" to zipCode,
                "country" to country
            )
        )

        val coordinatesObject = JSONObject(
            mapOf(
                "lat" to coordinates.latitude,
                "long" to coordinates.longitude
            )
        )

        val addressObject = JSONObject(
            mapOf(
                "fullAddress" to addressDetails,
                "coordinates" to coordinatesObject
            )
        )

        return JSONObject(mapOf("addresses" to JSONArray(listOf(addressObject)))).toString()
    }

    private fun patchUserAddress(userId: String, addressJson: String, callback: (Boolean) -> Unit) {
        val client = OkHttpClient.Builder()
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS) // Connection timeout
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS) // Read timeout
            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS) // Write timeout
            .build()

        Log.d("AddressJson", addressJson) // Log the JSON payload for debugging

        val requestBody = addressJson.toRequestBody("application/json".toMediaTypeOrNull())
        Log.d("url","https://b2c-backend-eik4.onrender.com/api/v1/customer/user/$userId")
        val request = Request.Builder()
            .url("https://b2c-backend-eik4.onrender.com/api/v1/customer/user/$userId") // Use dynamic userId
            .patch(requestBody)
            .addHeader("Content-Type", "application/json") // Ensure JSON is sent properly
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Failed to update address", Toast.LENGTH_SHORT).show()
                    callback(false)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                Handler(Looper.getMainLooper()).post {
                    val responseBody = response.body?.string() ?: "Unknown error"
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Address updated successfully!", Toast.LENGTH_SHORT).show()
                        callback(true)
                    } else {
                        Toast.makeText(context, "Failed to update address: $responseBody", Toast.LENGTH_LONG).show()
                        Log.d("responseBody", responseBody)
                    }
                    Log.d("Response Code", response.code.toString())
                }
            }
        })
    }



}

