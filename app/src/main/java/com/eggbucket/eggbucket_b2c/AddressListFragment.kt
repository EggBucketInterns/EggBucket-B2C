package com.eggbucket.eggbucket_b2c

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class UpdateUserRequest(
    val removeAddr: Int
)

class AddressListFragment : Fragment() {

    private lateinit var recyclerViewAddresses: RecyclerView
    private lateinit var addressListAdapter: AddressAdapter
    private val addressList = ArrayList<UserAddress>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_address, container, false)
        val addaddressBtn=view.findViewById<Button>(R.id.add_button)
        val backBtn=view.findViewById<ImageView>(R.id.backBtn)

        recyclerViewAddresses = view.findViewById(R.id.BuyAgainRecyclerView)
        recyclerViewAddresses.layoutManager = LinearLayoutManager(requireContext())

        addressListAdapter = AddressAdapter(addressList, ::deleteAddressAt,::addAddres)
        recyclerViewAddresses.adapter = addressListAdapter

        fetchUserData("916363894956")

        addaddressBtn.setOnClickListener{
            findNavController().navigate(R.id.action_addressListFragment_to_mapFragment)
        }
        backBtn.setOnClickListener{
            findNavController().popBackStack()
        }

        return view
    }
    private fun addAddres(addr: String) {
        // Send the address back using FragmentResult
        val result = Bundle().apply {
            putString("selected_address", addr)
        }
        parentFragmentManager.setFragmentResult("address_request_key", result)

        // Navigate back to the previous fragment (CartFragment)
        requireActivity().onBackPressed()
    }



    private fun fetchUserData(phone: String) {
        val progressBar = view?.findViewById<ProgressBar>(R.id.progressBar)
        val recyclerview = view?.findViewById<RecyclerView>(R.id.BuyAgainRecyclerView)

        // Show progress bar before starting the call
        recyclerview?.visibility = View.GONE
        progressBar?.visibility = View.VISIBLE

        RetrofitClient.apiService.getUserByPhone(phone).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                // Hide progress bar when response is received
                progressBar?.visibility = View.GONE
                recyclerview?.visibility = View.VISIBLE

                if (response.isSuccessful) {
                    response.body()?.let { user ->
                        user.addresses?.let { addresses ->
                            if (addresses.isNotEmpty()) {
                                addressList.clear()
                                addressList.addAll(addresses)
                                println("Current Addresses for user: $addressList")
                                if (isAdded && !isDetached) {
                                    addressListAdapter.notifyDataSetChanged()
                                }
                            } else {
                                Toast.makeText(requireContext(), "No addresses found", Toast.LENGTH_LONG).show()
                            }
                        } ?: run {
                            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                // Hide progress bar on failure as well
                progressBar?.visibility = View.GONE

                Log.e("fetchUserData", "Failed: ${t.message}", t)
                if (isAdded && !isDetached) {
                    Toast.makeText(requireContext(), "Failed: ${t.message}", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun deleteAddressAt(position: Int) {
        val removeAddressRequest = UpdateUserRequest(removeAddr = position)

        RetrofitClient1.apiService1.updateUser("916363894956", removeAddressRequest)
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



}
