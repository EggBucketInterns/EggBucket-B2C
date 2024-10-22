package com.eggbucket.eggbucket_b2c

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartFragment : Fragment() {

    private lateinit var cartItemsRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var emptyCartButton: Button
    private lateinit var continueToPayButton: Button

    private val cartItems = mutableListOf(
        CartItem("Eggs x 6", 1, 50.0),
        CartItem("Eggs x 2", 2, 40.0),
        CartItem("Eggs x 1", 1, 30.0)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.cart_page, container, false)

        // Initialize UI elements
        cartItemsRecyclerView = view.findViewById(R.id.recyclerCartItems)
        emptyCartButton = view.findViewById(R.id.empty_cart_button)
        continueToPayButton = view.findViewById(R.id.continue_to_pay)

        setupRecyclerView()
        setupButtons()

        // Apply insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.cartMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        updateTotalPrice()

        return view
    }

    fun updateAddress(newAddress: String) {
        // Handle the new address (e.g., update UI or internal data)
        Toast.makeText(requireContext(), "Address updated: $newAddress", Toast.LENGTH_SHORT).show()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(cartItems, ::onQuantityChanged, ::onRemoveItem)
        cartItemsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        cartItemsRecyclerView.adapter = cartAdapter
    }

    private fun setupButtons() {
        emptyCartButton.setOnClickListener {
            cartItems.clear()
            cartAdapter.notifyDataSetChanged()
            updateTotalPrice()
        }

        continueToPayButton.setOnClickListener {
            // Handle order placement logic
            cartItems.clear()
        }
    }

    private fun onQuantityChanged() {
        updateTotalPrice()
    }

    private fun onRemoveItem(item: CartItem) {
        cartItems.remove(item)
        cartAdapter.notifyDataSetChanged()
        updateTotalPrice()
    }

    private fun updateTotalPrice() {
        val total = cartItems.sumOf { it.quantity * it.price }
        continueToPayButton.text = "CONTINUE TO PAY ₹$total"
    }
}
