package com.eggbucket.eggbucket_b2c


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class CartActivity : AppCompatActivity() {

    private lateinit var cartItemsRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var emptyCartButton: Button


    private lateinit var continueToPayButton: Button
    private val cartItems = mutableListOf<CartItem>(
        CartItem("Eggs x 6", 1, 50.0 ),
        CartItem("Eggs x 2", 2, 40.0 ),
        CartItem("Eggs x 1", 1, 30.0)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.cart_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cartMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        cartItemsRecyclerView = findViewById(R.id.recyclerCartItems)
        emptyCartButton = findViewById(R.id.empty_cart_button)
        continueToPayButton = findViewById(R.id.continue_to_pay)


        cartAdapter = CartAdapter(cartItems, ::onQuantityChanged, ::onRemoveItem)
        cartItemsRecyclerView.layoutManager = LinearLayoutManager(this)
        cartItemsRecyclerView.adapter = cartAdapter

        emptyCartButton.setOnClickListener {
            cartItems.clear()
            cartAdapter.notifyDataSetChanged()
            updateTotalPrice()
        }

        // Continue to pay button listener
        continueToPayButton.setOnClickListener {
            //to do place order function
            cartItems.clear()
        }

        updateTotalPrice()
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