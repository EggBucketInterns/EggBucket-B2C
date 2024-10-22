package com.eggbucket.eggbucket_b2c

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
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
    private lateinit var addressText: TextView
    private lateinit var changeAddressButton: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var continueToPayButton: Button

    private val cartItems = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.cart_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cartMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)

        // Retrieve counts after initializing SharedPreferences
        val count1 = sharedPreferences.getInt("count1", 0)
        val count2 = sharedPreferences.getInt("count2", 0)
        val count3 = sharedPreferences.getInt("count3", 0)

        // Initialize cart items with retrieved counts
        cartItems.add(CartItem("Eggs x 6", count1, 50.0))
        cartItems.add(CartItem("Eggs x 2", count2, 40.0))
        cartItems.add(CartItem("Eggs x 1", count3, 30.0))

        cartItemsRecyclerView = findViewById(R.id.recyclerCartItems)
        emptyCartButton = findViewById(R.id.empty_cart_button)
        continueToPayButton = findViewById(R.id.continue_to_pay)
        addressText = findViewById(R.id.delivery_address)
        changeAddressButton = findViewById(R.id.change_address)

        // Create the CartAdapter with an additional parameter for updating quantity
        cartAdapter = CartAdapter(cartItems, ::onQuantityChanged, ::onRemoveItem, ::updateQuantityInSharedPreferences)
        cartItemsRecyclerView.layoutManager = LinearLayoutManager(this)
        cartItemsRecyclerView.adapter = cartAdapter

        emptyCartButton.setOnClickListener {
            cartItems.clear()
            cartAdapter.notifyDataSetChanged()
            updateQuantitiesInSharedPreferences(0, 0, 0)
            updateTotalPrice()
        }

        changeAddressButton.setOnClickListener {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(android.R.id.content, AddressListFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        // Continue to pay button listener
        continueToPayButton.setOnClickListener {
            // TODO: Implement place order functionality
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

    fun updateAddress(addr: String) {
        addressText.text = "Address: $addr"
    }

    private fun updateTotalPrice() {
        val total = cartItems.sumOf { it.quantity * it.price }
        continueToPayButton.text = "CONTINUE TO PAY ₹$total"
    }

    // Function to update quantity in SharedPreferences
    private fun updateQuantityInSharedPreferences(itemName: String, quantity: Int) {
        val editor = sharedPreferences.edit()
        when (itemName) {
            "Eggs x 6" -> editor.putInt("count1", quantity)
            "Eggs x 2" -> editor.putInt("count2", quantity)
            "Eggs x 1" -> editor.putInt("count3", quantity)
        }
        editor.apply()
    }

    private fun updateQuantitiesInSharedPreferences(count1: Int, count2: Int, count3: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("count1", count1)
        editor.putInt("count2", count2)
        editor.putInt("count3", count3)
        editor.apply()
    }
}

