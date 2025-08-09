package com.eggbucket.eggbucket_b2c

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddressListFragment : Fragment() {

    private lateinit var recyclerViewAddresses: RecyclerView
    private lateinit var addressListAdapter: AddressAdapter
    private val addressList = ArrayList<UserAddress>()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var phoneNumber: String
    private lateinit var progressBar: ProgressBar
    private lateinit var noaddress:TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_address, container, false)
        val addaddressBtn = view.findViewById<Button>(R.id.add_New_Address)
        val backBtn = view.findViewById<ImageView>(R.id.backBtn)
        noaddress=view.findViewById(R.id.noaddresstext)
        noaddress.visibility=View.INVISIBLE
        progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        phoneNumber= sharedPreferences.getString("user_phone","916363894956").toString()
        recyclerViewAddresses = view.findViewById(R.id.BuyAgainRecyclerView)
        recyclerViewAddresses.layoutManager = LinearLayoutManager(requireContext())

        addressListAdapter = AddressAdapter(addressList, ::deleteAddressAt, ::saveAddress)
        recyclerViewAddresses.adapter = addressListAdapter

        fetchUserData(phoneNumber)

        addaddressBtn.setOnClickListener {
            findNavController().navigate(R.id.action_addressListFragment_to_mapFragment)
        }

        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }

    private fun fetchUserData(phone: String) {
        progressBar.visibility = View.VISIBLE

        RetrofitClient.apiService.getUserByPhone(phone).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                progressBar.visibility = View.INVISIBLE
                if (response.isSuccessful) {
                    response.body()?.let { user ->
                        user.addresses?.let { addresses ->
                            addressList.clear()
                            addressList.addAll(addresses)
                            Log.d("order", addressList.toString())
                            addressList.forEach { address ->
                                Log.d("Coordinates", "Lat: ${address.coordinates.lat}, Long: ${address.coordinates.long}")
                            }
                            addressListAdapter.notifyDataSetChanged()
                        }
                    }
                } else {
                    if (response.code()==404){
                        noaddress.visibility=View.VISIBLE
                    }

                    Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                progressBar.visibility = View.INVISIBLE
                Log.e("fetchUserData", "Failed: ${t.message}", t)
                Toast.makeText(requireContext(), "Failed: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun deleteAddressAt(position: Int) {
        val removeAddressRequest = UpdateUserRequest(index = position)

        RetrofitClient1.apiService1.updateUser(phoneNumber, removeAddressRequest)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        addressList.removeAt(position)
                        addressListAdapter.notifyItemRemoved(position)
                        Toast.makeText(requireContext(), "Address removed", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("AddressListFragment", "Delete failed: ${t.message}")
                    Toast.makeText(requireContext(), "Failed: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun saveAddress(address: UserAddress) {
        val gson = Gson()
        val addressJson = gson.toJson(address)
        Log.d("order", addressJson)
        sharedPreferences.edit().putString("selected_address", addressJson).apply()
        Toast.makeText(requireContext(), "Address saved", Toast.LENGTH_SHORT).show()

        // Go back to the previous fragment
        findNavController().popBackStack()
    }
}

class UpdateUserRequest(index: Int) {
    var removeAddr: Int = index
}

