package com.eggbucket.eggbucket_b2c

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eggbucket.eggbucket_b2c.databinding.FragmentOrderHistoryBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.firebase.Timestamp

class OrderHistory : Fragment() {
    private var _binding: FragmentOrderHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private val orderList = ArrayList<OrderItem>()
    private lateinit var sharedPref: SharedPreferences
    private var userPhone: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)

        sharedPref = requireActivity().getSharedPreferences("MyPreferences", MODE_PRIVATE)
        userPhone = sharedPref.getString("user_phone", null)

        setupViews()
        userPhone?.let { fetchOrdersFromApi(it.replace("+", "")) }
        return binding.root
    }

    private fun setupViews() {
        setupRecyclerView()
        setupBackNavigation()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            orderHistoryAdapter = OrderHistoryAdapter(orderList)
            adapter = orderHistoryAdapter
        }
    }

    private fun setupBackNavigation() {
        binding.backIcon.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun fetchOrdersFromApi(phone: String) {
        RetrofitClient.apiService.getPreviousOrders(customerId = phone)
            .enqueue(object : Callback<OrderResponse> {
                override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                    if (response.isSuccessful) {
                        // println("Response: ${response.body()}")
                        response.body()?.let { orderResponse ->
                            processOrders(orderResponse)
                        }
                    } else {
                        showError("Failed to load orders")
                    }
                }

                override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                    showError("Error: ${t.message}")
                }
            })
    }

    private fun processOrders(orderResponse: OrderResponse) {
        orderList.clear()
        orderResponse.orders.forEach { order ->
            order.products.forEach { (productCategory, value) ->
                // Ensure non-null `deliveryPartnerId` in the copy
                val orderCopy = order.copy(
                    products = mapOf(productCategory to value),
                    deliveryPartnerId = order.deliveryPartnerId ?: "Unknown"  // Provide a default if null
                )
                orderList.add(orderCopy)
            }
        }
        orderHistoryAdapter.notifyDataSetChanged()
    }


    private fun showError(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = OrderHistory()
    }
}