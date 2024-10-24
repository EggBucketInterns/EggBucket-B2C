package com.eggbucket.eggbucket_b2c

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
    private lateinit var footer:LinearLayout
    private lateinit var cartscroll: ScrollView
    private lateinit var cartempty: TextView

    private val cartItems = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retain this fragment across configuration changes and navigation

        retainInstance = true

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("MyPreferences", 0)

        // Populate the cartItems only if the list is empty (to avoid duplication)
        if (cartItems.isEmpty()) {
            // Retrieve counts from SharedPreferences
            val count1 = sharedPreferences.getInt("count1", 0)
            val count2 = sharedPreferences.getInt("count2", 0)
            val count3 = sharedPreferences.getInt("count3", 0)


            // Do not add items if the count is zero
            if (count1 > 0) {
                cartItems.add(CartItem("image6","Eggs x 6" ,count1, 60.0))
            }
            if (count2 > 0) {
                cartItems.add(CartItem("image12","Eggs x 12", count2, 120.0))
            }
            if (count3 > 0) {
                cartItems.add(CartItem("image30","Eggs x 30", count3, 300.0))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.cart_page, container, false)
        val back_Btn=view.findViewById<ImageView>(R.id.back_button)

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.cartMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        cartItemsRecyclerView = view.findViewById(R.id.recyclerCartItems)
        emptyCartButton = view.findViewById(R.id.empty_cart_button)
        continueToPayButton = view.findViewById(R.id.continue_to_pay)
        addressText = view.findViewById(R.id.delivery_address)
        changeAddressButton = view.findViewById(R.id.change_address)
        footer=view.findViewById(R.id.liniar_layout_cart_foouter)
        cartscroll=view.findViewById(R.id.scroll_view_cart)
        cartempty=view.findViewById(R.id.cart_empty)

        if(cartItems.isNotEmpty()){
            cartempty.visibility=View.GONE
            cartscroll.visibility=View.VISIBLE
            footer.visibility=View.VISIBLE
        }
        // Set up the CartAdapter
        cartAdapter = CartAdapter(cartItems, ::onQuantityChanged, ::onRemoveItem, ::updateQuantityInSharedPreferences)
        cartItemsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        cartItemsRecyclerView.adapter = cartAdapter

        // Set up listeners
        emptyCartButton.setOnClickListener {
            cartItems.clear()
            cartAdapter.notifyDataSetChanged()
            ClearSharedPreferences()
            updateTotalPrice()
            footer.visibility=View.GONE
            cartscroll.visibility=View.GONE
            cartempty.visibility=View.VISIBLE
        }
        parentFragmentManager.setFragmentResultListener("address_request_key", viewLifecycleOwner) { _, bundle ->
            val selectedAddress = bundle.getString("selected_address")
            selectedAddress?.let {
                updateAddress(it)
            }
        }

        changeAddressButton.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_addressListFragment)
        }

        continueToPayButton.setOnClickListener {
            cartItems.clear()
        }
        back_Btn.setOnClickListener{
            findNavController().popBackStack()
        }

        // Update the total price
        updateTotalPrice()

        return view
    }

    private fun onQuantityChanged(item:String,newQuantity:Int){
        updateQuantityInSharedPreferences(item,newQuantity)
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
            "Eggs x 12" -> editor.putInt("count2", quantity)
            "Eggs x 30" -> editor.putInt("count3", quantity)
        }
        editor.apply()
    }

    private fun ClearSharedPreferences() {
        val editor = sharedPreferences.edit()
        editor.putInt("count1", 0)
        editor.putInt("count2", 0)
        editor.putInt("count3", 0)
        editor.apply()



    }




}



