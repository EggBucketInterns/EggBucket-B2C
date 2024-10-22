package com.eggbucket.eggbucket_b2c

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eggbucket.eggbucket_b2c.databinding.FragmentOrderHistoryBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderHistory : Fragment() {
    private var _binding: FragmentOrderHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private val orderList = ArrayList<OrderItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)

        setupViews()
        fetchOrdersFromApi()
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

    private fun fetchOrdersFromApi() {
        RetrofitClient.apiService.getPreviousOrders(customerId = "1111111112")
            .enqueue(object : Callback<OrderResponse> {
                override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                    if (response.isSuccessful) {
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
                orderList.add(order.copy(products = mapOf(productCategory to value)))
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