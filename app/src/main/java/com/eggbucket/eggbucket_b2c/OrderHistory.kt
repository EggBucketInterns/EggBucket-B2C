package com.eggbucket.eggbucket_b2c

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderHistory : Fragment() {

    private lateinit var recyclerViewOrders: RecyclerView
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private val orderList = ArrayList<OrderItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        recyclerViewOrders = view.findViewById(R.id.recyclerViewOrders)
        recyclerViewOrders.layoutManager = LinearLayoutManager(requireContext())

//        val orderList = listOf(
//            OrderItem(R.drawable.eggimage, "Eggs x 6", "Ordered on 16th June", "Delivered", "Rs. 80"),
//            OrderItem(R.drawable.eggimage1, "Milk x 2", "Ordered on 10th June", "Pending", "Rs. 40"),
//            OrderItem(R.drawable.eggimage2, "Bread x 1", "Ordered on 8th June", "Pending", "Rs. 30")
//        )


        val backIcon: ImageView = view.findViewById(R.id.backIcon)
        backIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }
        fetchOrdersFromApi()
        orderHistoryAdapter = OrderHistoryAdapter(orderList)
        println("OrderList outside function: $orderList")
        recyclerViewOrders.adapter = orderHistoryAdapter

        return view
    }

    private fun fetchOrdersFromApi() {
        RetrofitClient.apiService.getPreviousOrders(customerId = "1111111112")
            .enqueue(object : Callback<OrderResponse> {
                override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                    if (response.isSuccessful){
                        val orderResponse = response.body()
                        val ordersByDate = orderResponse!!.orders
                        println("Orders By Date: $ordersByDate")
                        for (order in ordersByDate){
                            for ((productCategory, value) in order.products) {
                                val p = mapOf(productCategory to value)
                                val newOrder = order.copy(products = p)
                                orderList.add(newOrder)
                            }
                        }
                        orderHistoryAdapter.notifyDataSetChanged()

//                        orderList.addAll(orderResponse!!.orders)
//                        println("OrderList within function: $orderList")
                    } else {
                        Toast.makeText(requireContext(), "Failed to load orders", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                    // Handle failure
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()

                }
            })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment.
         */
        @JvmStatic
        fun newInstance() = OrderHistory()
    }
}
