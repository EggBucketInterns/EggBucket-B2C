package com.eggbucket.eggbucket_b2c

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView

class OrderHistory : Fragment() {

    private lateinit var recyclerViewOrders: RecyclerView
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        recyclerViewOrders = view.findViewById(R.id.recyclerViewOrders)
        recyclerViewOrders.layoutManager = LinearLayoutManager(requireContext())

        val orderList = listOf(
            OrderItem(R.drawable.eggimage, "Eggs x 6", "Ordered on 16th June", "Delivered", "Rs. 80"),
            OrderItem(R.drawable.eggimage1, "Milk x 2", "Ordered on 10th June", "Pending", "Rs. 40"),
            OrderItem(R.drawable.eggimage2, "Bread x 1", "Ordered on 8th June", "Pending", "Rs. 30")
        )

        orderHistoryAdapter = OrderHistoryAdapter(orderList)
        recyclerViewOrders.adapter = orderHistoryAdapter

        val backIcon: ImageView = view.findViewById(R.id.backIcon)
        backIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return view
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
