package com.eggbucket.eggbucket_b2c

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.play.core.integrity.i

class CartFragment : Fragment() {

    private lateinit var cartItemsRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var emptyCartButton: Button
    private lateinit var addressText: TextView
    private lateinit var changeAddressButton: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var continueToPayButton: Button

    private val cartItems = mutableListOf<CartItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.cart_page, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.cartMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("MyPreferences", 0)

        // Retrieve counts from SharedPreferences
        val count1 = sharedPreferences.getInt("count1", 0)
        val count2 = sharedPreferences.getInt("count2", 0)
        val count3 = sharedPreferences.getInt("count3", 0)
        //do not add if count is zero
        if(count1>0){
            cartItems.add(CartItem("Eggs x 6", count1, 50.0))
        }
        if(count2>0){
            cartItems.add(CartItem("Eggs x 2", count2, 40.0))
        }
        if(count3>0){
            cartItems.add(CartItem("Eggs x 1", count3, 30.0))
        }


        cartItemsRecyclerView = view.findViewById(R.id.recyclerCartItems)
        emptyCartButton = view.findViewById(R.id.empty_cart_button)
        continueToPayButton = view.findViewById(R.id.continue_to_pay)
        addressText = view.findViewById(R.id.delivery_address)
        changeAddressButton = view.findViewById(R.id.change_address)

        // Set up the CartAdapter
        cartAdapter = CartAdapter(cartItems, ::onQuantityChanged, ::onRemoveItem, ::updateQuantityInSharedPreferences)
        cartItemsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        cartItemsRecyclerView.adapter = cartAdapter

        // Set up listeners
        emptyCartButton.setOnClickListener {
            cartItems.clear()
            cartAdapter.notifyDataSetChanged()
            updateQuantitiesInSharedPreferences(0, 0, 0)
            updateTotalPrice()
        }

        changeAddressButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(android.R.id.content, AddressListFragment())
                .addToBackStack(null)
                .commit()
        }

        continueToPayButton.setOnClickListener {
            // TODO: Implement place order functionality
            cartItems.clear()
        }

        updateTotalPrice()

        return view
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


