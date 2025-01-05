package com.eggbucket.eggbucket_b2c


import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import android.Manifest
import android.widget.ProgressBar
import android.widget.RelativeLayout
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.util.concurrent.TimeUnit


class CartFragment : Fragment() {

    private lateinit var cartItemsRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var emptyCartButton: Button
    private lateinit var addressText: TextView
    private lateinit var progressOverlay: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var changeAddressButton: ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var continueToPayButton: Button
    private lateinit var footer: LinearLayout
    private lateinit var cartscroll: ScrollView
    private lateinit var cartempty: TextView
    private lateinit var phoneNumber: String

    private val cartItems = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        // Initialize SharedPreferences and phone number
        sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        phoneNumber= sharedPreferences.getString("user_phone","916363894956").toString()
        Log.d("phonenumber", phoneNumber)

        // Populate cartItems if empty to avoid duplication
        if (cartItems.isEmpty()) {
            val count1 = sharedPreferences.getInt("count1", 0)
            val count2 = sharedPreferences.getInt("count2", 0)
            val count3 = sharedPreferences.getInt("count3", 0)
            //only add if item exist
            if (count1 > 0) cartItems.add(CartItem("image6", "Eggs x 6", count1, 60.0))
            if (count2 > 0) cartItems.add(CartItem("image12", "Eggs x 12", count2, 120.0))
            if (count3 > 0) cartItems.add(CartItem("image30", "Eggs x 30", count3, 300.0))
        }
    }
    private val REQUEST_CODE = 100

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send notification
                val notificationHelper = NotificationHelper(requireContext())
                notificationHelper.createNotificationChannel()
                notificationHelper.sendNotification("12345")
            } else {
                // Permission denied, handle accordingly (e.g., inform user)
                Toast.makeText(requireContext(), "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun triggerNotification(orderId: String) {
        println("THIS IS CALLED")
        val notificationHelper = NotificationHelper(requireContext())
        notificationHelper.createNotificationChannel()
        notificationHelper.sendNotification(orderId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.cart_page, container, false)

        // Initialize views
        cartItemsRecyclerView = view.findViewById(R.id.recyclerCartItems)
        emptyCartButton = view.findViewById(R.id.empty_cart_button)
        progressOverlay = view.findViewById(R.id.progress_overlay)
        progressBar = view.findViewById(R.id.progress_bar)
        continueToPayButton = view.findViewById(R.id.continue_to_pay)
        addressText = view.findViewById(R.id.delivery_address)
        changeAddressButton = view.findViewById<ImageView>(R.id.change_address)
        footer = view.findViewById(R.id.liniar_layout_cart_foouter)
        cartscroll = view.findViewById(R.id.scroll_view_cart)
        cartempty = view.findViewById(R.id.cart_empty)

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.cartMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Check for saved address in SharedPreferences
        val addressJson = sharedPreferences.getString("selected_address", null)
        //updating address
        if (addressJson != null) {
            val address = Gson().fromJson(addressJson, UserAddress::class.java)
            updateAddress(address) // Updated method call with UserAddress type
        } else {
            Log.d("Saved Address", "No address found")
        }
        //display cart item empty
        if (cartItems.isNotEmpty()) {
            cartempty.visibility = View.GONE
            cartscroll.visibility = View.VISIBLE
            footer.visibility = View.VISIBLE
        }

        // Set up CartAdapter
        cartAdapter = CartAdapter(cartItems, ::onQuantityChanged, ::onRemoveItem, ::updateQuantityInSharedPreferences)
        cartItemsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        cartItemsRecyclerView.adapter = cartAdapter


        // empty cart function listner
        emptyCartButton.setOnClickListener {
            clearcart()
        }
        //update address on click  if only string
//        parentFragmentManager.setFragmentResultListener("address_request_key", viewLifecycleOwner) { _, bundle ->
//            val selectedAddress = bundle.getString("selected_address")
//            selectedAddress?.let {
//                val address = Gson().fromJson(it, UserAddress::class.java)
//                updateAddress(address)
//            }
//        }
        //listener for change address button
        changeAddressButton.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_addressListFragment)
        }
        //continue to pay listener

        continueToPayButton.setOnClickListener {
            showProgress()

            val addressJson = sharedPreferences.getString("selected_address", null)
            if (addressJson == null){
                findNavController().navigate(R.id.action_cartFragment_to_mapFragment)
                Toast.makeText(requireContext(), "Choose an Address", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val address = Gson().fromJson(addressJson, UserAddress::class.java)
            //create json format address and coordinates
            val fullAddress = JSONObject().apply {
                put("flatNo", address.fullAddress.flatNo)
                put("area", address.fullAddress.area)
                put("city", address.fullAddress.city)
                put("state", address.fullAddress.state)
                put("zipCode", address.fullAddress.zipCode)
                put("country", address.fullAddress.country)
            }
            val coordinates = JSONObject().apply {
                put("lat", address.coordinates.lat)
                put("long", address.coordinates.long)
            }


            // Generate product data from cart items
            val products = JSONObject()
            cartItems.forEach { item ->
                when (item.name) {
                    "Eggs x 6" -> products.put("E6", item.quantity)
                    "Eggs x 12" -> products.put("E12", item.quantity)
                    "Eggs x 30" -> products.put("E30", item.quantity)
                }
            }

            val totalAmount = cartItems.sumOf { it.quantity * it.price }.toInt()
            //Function call for place order
            createOrder(
                apiUrl = "https://b2c-49u4.onrender.com",
                fullAddress = fullAddress,
                coordinates = coordinates,
                amount = totalAmount,
                products = products,
                customerId = phoneNumber
            )



        }


        view.findViewById<ImageView>(R.id.back_button).setOnClickListener {
            findNavController().popBackStack()
        }

        // Update the total price
        updateTotalPrice()

        return view
    }

    private fun showProgress() {
        progressOverlay.visibility = View.VISIBLE
    }

    //update price on quantity change to call from adapter
    private fun onQuantityChanged(item: String, newQuantity: Int) {
        updateQuantityInSharedPreferences(item, newQuantity)
        updateTotalPrice()
    }
    //remove item from cart
    private fun onRemoveItem(item: CartItem) {
        cartItems.remove(item)
        cartAdapter.notifyDataSetChanged()
        updateTotalPrice()
    }
    //delete all item from cart
    private fun clearcart() {
        activity?.runOnUiThread {
            cartItems.clear()
            cartAdapter.notifyDataSetChanged()
            clearSharedPreferences()
            updateTotalPrice()
            footer.visibility = View.GONE
            cartscroll.visibility = View.GONE
            cartempty.visibility = View.VISIBLE
        }
    }
    //display address of shared preference in required format
    private fun updateAddress(address: UserAddress) {
        val displayAddress = "${address.fullAddress.flatNo}, ${address.fullAddress.area}, ${address.fullAddress.city}"
        addressText.text = "$displayAddress"
    }
    //calculate total prise
    private fun updateTotalPrice() {
        val total = cartItems.sumOf { it.quantity * it.price }
        continueToPayButton.text = "CONTINUE TO PAY ₹$total"
    }
    // update quantity of specific item
    private fun updateQuantityInSharedPreferences(itemName: String, quantity: Int) {
        val editor = sharedPreferences.edit()
        when (itemName) {
            "Eggs x 6" -> editor.putInt("count1", quantity)
            "Eggs x 12" -> editor.putInt("count2", quantity)
            "Eggs x 30" -> editor.putInt("count3", quantity)
        }
        editor.apply()
    }

    //clear all data of item from shared preferences
    private fun clearSharedPreferences() {
        sharedPreferences.edit().apply {
            putInt("count1", 0)
            putInt("count2", 0)
            putInt("count3", 0)
            apply()
        }
    }


    //function to create order and store it in database
    private fun createOrder(
        apiUrl: String,
        fullAddress: JSONObject,
        coordinates: JSONObject,
        amount: Int,
        products: JSONObject,
        customerId: String
    ) {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS) // Set connection timeout
            .readTimeout(10, TimeUnit.SECONDS)    // Set read timeout
            .writeTimeout(10, TimeUnit.SECONDS)   // Set write timeout
            .build()


        // Create the JSON body
        val bodyJson = JSONObject().apply {
            put("address", JSONObject().apply {
                put("fullAddress", fullAddress)
                put("coordinates", coordinates)
            })
            put("amount", amount)
            put("products", products)
            put("customerId", customerId)
        }
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            bodyJson.toString()
        )


        // Create the request
        val request = Request.Builder()
            .url("$apiUrl/api/v1/order/order")
            .post(requestBody)
            .build()

        // Execute the request
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Order creation failed. Please try again.", Toast.LENGTH_LONG).show()
                    progressOverlay.visibility = View.GONE
                    findNavController().navigate(R.id.action_cartFragment_self) // Redirect to CartFragment
                }
            }

            override fun onResponse(call: Call, response: Response) {
                activity?.runOnUiThread {
                    if (response.isSuccessful) {
                        // Handle successful order creation
                        progressOverlay.visibility = View.GONE
                        Toast.makeText(requireContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_cartFragment_to_orderCompleted)
                    } else {
                        // Handle server-side errors
                        progressOverlay.visibility = View.GONE
                        Toast.makeText(requireContext(), "Failed to create order. Please try again.", Toast.LENGTH_LONG).show()
                        findNavController().navigate(R.id.action_cartFragment_self) // Redirect to CartFragment
                    }
                }
            }

        })
    }
    //dialog to show result
    private fun showAlertDialog(title: String, message: String) {
        activity?.runOnUiThread {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }
    companion object {
        private const val NOTIFICATION_PERMISSION_CODE = 1001
    }
}
