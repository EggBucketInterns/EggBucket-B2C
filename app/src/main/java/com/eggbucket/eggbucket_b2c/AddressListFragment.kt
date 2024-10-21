package com.eggbucket.eggbucket_b2c

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddressListFragment : Fragment() {

    private lateinit var recyclerViewAddresses: RecyclerView
    private lateinit var addressListAdapter: AddressAdapter
    private val addressList = ArrayList<UserAddress>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_address, container, false)

        recyclerViewAddresses = view.findViewById(R.id.BuyAgainRecyclerView)
        recyclerViewAddresses.layoutManager = LinearLayoutManager(requireContext())


        fetchUserData("1111111111")
        addressListAdapter = AddressAdapter(addressList)
        recyclerViewAddresses.adapter = addressListAdapter

        val backIcon: ImageView = view.findViewById(R.id.imageView4)
        backIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return view
    }



    private fun fetchUserData(phone: String) {
        RetrofitClient.apiService.getUserByPhone(phone).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    addressList.addAll(user!!.userAddresses)
                    println("Addresses: $addressList")
//                    user?.let {
//                        // Update UI with user info
//                        binding.tvUserName.text = it.name
//                        binding.tvUserEmail.text = it.email
//
//                        // Pass the address list to the adapter
//                        addressAdapter.updateAddresses(it.addresses)
//                    }
                    addressListAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(requireContext(), "Failed: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
