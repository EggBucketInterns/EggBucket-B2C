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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import android.Manifest
import android.content.Intent
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.lifecycle.lifecycleScope
import com.eggbucket.eggbucket_b2c.uiscreens.GetInfo
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.HttpURLConnection
import java.net.URL
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

    // Cart items list; initially prices are set as hardcoded defaults.
    // They will be updated dynamically after fetching product details.
    private val cartItems = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        // Initialize SharedPreferences and phone number
        sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        phoneNumber = sharedPreferences.getString("user_phone", "916363894956").toString()
        Log.d("phonenumber", phoneNumber)

        // Populate cartItems if empty to avoid duplication.
        // The price values here are temporary defaults.
        if (cartItems.isEmpty()) {
            val count1 = sharedPreferences.getInt("count1", 0)
            val count2 = sharedPreferences.getInt("count2", 0)
            val count3 = sharedPreferences.getInt("count3", 0)
            // Only add if item exists in SharedPreferences.
            if (count1 > 0) cartItems.add(CartItem("image6", "Eggs x 6", count1, 0.0))
            if (count2 > 0) cartItems.add(CartItem("image12", "Eggs x 12", count2, 0.0))
            if (count3 > 0) cartItems.add(CartItem("image30", "Eggs x 30", count3, 0.0))
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

        makeApiRequestWithRetries2(phoneNumber)

        // Check for saved address in SharedPreferences
        val addressJson = sharedPreferences.getString("selected_address", null)
        if (addressJson != null) {
            val address = Gson().fromJson(addressJson, UserAddress::class.java)
            updateAddress(address)
        } else {
            Log.d("Saved Address", "No address found")
        }

        // Show/hide views based on cart items existence
        if (cartItems.isNotEmpty()) {
            cartempty.visibility = View.GONE
            cartscroll.visibility = View.VISIBLE
            footer.visibility = View.VISIBLE
        }

        // Set up CartAdapter
        cartAdapter = CartAdapter(cartItems, ::onQuantityChanged, ::onRemoveItem, ::updateQuantityInSharedPreferences)
        cartItemsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        cartItemsRecyclerView.adapter = cartAdapter

        // Empty cart listener
        emptyCartButton.setOnClickListener {
            clearcart()
        }

        // Listener for change address button
        changeAddressButton.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_addressListFragment)
        }

        continueToPayButton.setOnClickListener {
            showProgress()
            if (!checkUserInfo()) {
                val intent = Intent(requireContext(), GetInfo::class.java)
                startActivity(intent)
                return@setOnClickListener
            }

            val addressJson = sharedPreferences.getString("selected_address", null)
            if (addressJson == null) {
                findNavController().navigate(R.id.action_cartFragment_to_mapFragment)
                Toast.makeText(requireContext(), "Choose an Address", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val address = Gson().fromJson(addressJson, UserAddress::class.java)
            // Create JSON format address and coordinates
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

            // Generate product data from cart items in the desired structure
            val products = JSONObject()

            // Map the "Eggs x N" cart label to (productId, nameInBackend)
            val productIdMap = mapOf(
                "Eggs x 6"  to Pair("0Xkt5nPNGubaZ9mMpzGs", "6pc_tray"),
                "Eggs x 12" to Pair("NVPDbCfymcyD7KpH6J5J", "12pc_tray"),
                "Eggs x 30" to Pair("a2MeuuaCweGQNBIc4l51", "30pc_tray")
            )

            cartItems.forEach { item ->
                // Look up the corresponding productId + backend name for the cart item
                val productInfo = productIdMap[item.name]
                if (productInfo != null) {
                    val (productId, apiName) = productInfo

                    // Build the inner JSON object
                    val productObj = JSONObject().apply {
                        put("name", apiName)        // e.g. "6pc_tray"
                        put("productId", productId) // e.g. "0Xkt5nPNGubaZ9mMpzGs"
                        put("quantity", item.quantity)
                    }
                    // Put that object in the 'products' JSON using productId as the key
                    products.put(productId, productObj)
                }
            }

            val totalAmount = cartItems.sumOf { it.quantity * it.price }.toInt()

            // Call createOrder with your final JSON
            createOrder(
                apiUrl = "https://b2c-backend-eik4.onrender.com",
                fullAddress = fullAddress,
                coordinates = coordinates,
                amount = totalAmount,
                products = products,  // <--- This now has the correct structure
                customerId = phoneNumber
            )
        }


        view.findViewById<ImageView>(R.id.back_button).setOnClickListener {
            findNavController().popBackStack()
        }

        // Update the total price initially
        updateTotalPrice()

        // Fetch dynamic product details to update prices and remove hardcoded values.
        fetchProductDetailsAndUpdatePrices()

        return view
    }

    private fun showProgress() {
        progressOverlay.visibility = View.VISIBLE
    }

    // Called when quantity changes from adapter
    private fun onQuantityChanged(item: String, newQuantity: Int) {
        updateQuantityInSharedPreferences(item, newQuantity)
        updateTotalPrice()
    }

    // Remove an item from the cart
    private fun onRemoveItem(item: CartItem) {
        cartItems.remove(item)
        cartAdapter.notifyDataSetChanged()
        updateTotalPrice()
    }

    // Clear the entire cart
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

    // Display the saved address in the required format
    private fun updateAddress(address: UserAddress) {
        val displayAddress = "${address.fullAddress.flatNo}, ${address.fullAddress.area}, ${address.fullAddress.city}"
        addressText.text = displayAddress
    }

    // Calculate total price and update the button text
    private fun updateTotalPrice() {
        val total = cartItems.sumOf { it.quantity * it.price }
        continueToPayButton.text = "PLACE ORDER OF ₹$total"
    }

    // Update quantity for a specific item in SharedPreferences
    private fun updateQuantityInSharedPreferences(itemName: String, quantity: Int) {
        val editor = sharedPreferences.edit()
        when (itemName) {
            "Eggs x 6" -> editor.putInt("count1", quantity)
            "Eggs x 12" -> editor.putInt("count2", quantity)
            "Eggs x 30" -> editor.putInt("count3", quantity)
        }
        editor.apply()
    }

    // Clear cart item data from SharedPreferences
    private fun clearSharedPreferences() {
        sharedPreferences.edit().apply {
            putInt("count1", 0)
            putInt("count2", 0)
            putInt("count3", 0)
            apply()
        }
    }

    private fun checkUserInfo(): Boolean {
        val firstName = sharedPreferences.getString("name", null)
        return !firstName.isNullOrEmpty()
    }

    // Function to create order and store it in the database
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
        Log.d("jsonbody", bodyJson.toString())
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
                    findNavController().navigate(R.id.action_cartFragment_self)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                activity?.runOnUiThread {
                    if (response.isSuccessful) {
                        Log.d("ordered place", response.body.toString())
                        triggerNotification("success")
                        clearcart()
                        progressOverlay.visibility = View.GONE
                        Toast.makeText(requireContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_cartFragment_to_orderCompleted)
                    } else {
                        showAlertDialog("Please change address", "We will expand to this location soon")
                        progressOverlay.visibility = View.GONE
                        Log.d("ordered place", response.message.toString())
                        Toast.makeText(requireContext(), "Failed to create order. Please try again.", Toast.LENGTH_LONG).show()
                        findNavController().navigate(R.id.action_cartFragment_self)
                    }
                }
            }
        })
    }

    // Show an alert dialog with a message
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

    // This function remains unchanged (example API request with retries for order creation)
    private fun makeApiRequestWithRetries2(phone: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // Updated URL with trailing slash
            val url = "https://b2c-backend-eik4.onrender.com/api/v1/order/order"
            var attempts = 0
            var success = false

            val requestBody = """
            {
                "id": "6363894956-1742048111738-43",
                  "address": {
                    "fullAddress": {
                      "flatNo": "491",
                      "area": "Marathahalli",
                      "city": "Bengaluru",
                      "state": "Karnataka",
                      "zipCode": "560037",
                      "country": "India"
                    },
                    "coordinates": {
                      "lat": 12.9650126187683,
                      "long": 77.7158141012058
                    }
                  },
                  "amount": 420,
                  "products": {[
                    "0Xkt5nPNGubaZ9mMpzGs": {
                      "name": "6pc_tray",
                      "productId": "0Xkt5nPNGubaZ9mMpzGs",
                      "quantity": 1
                    },
                    "a2MeuuaCweGQNBIc4l51": {
                      "name": "30pc_tray",
                      "productId": "a2MeuuaCweGQNBIc4l51",
                      "quantity": 2
                    }]
                  },
                  "customerId": "$phone"
                }
        """.trimIndent()

            while (attempts < 2 && !success) {
                try {
                    Log.d("API_REQUEST", "Attempt: ${attempts + 1}")
                    val connection = URL(url).openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.doOutput = true

                    connection.outputStream.use { outputStream ->
                        outputStream.write(requestBody.toByteArray(Charsets.UTF_8))
                    }

                    val responseCode = connection.responseCode
                    val responseMessage = connection.responseMessage
                    Log.d("API_RESPONSE", "Response Code: $responseCode, Message: $responseMessage")

                    // Treat HTTP_OK (200) or HTTP_CREATED (201) as success
                    if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                        success = true
                        val response = connection.inputStream.bufferedReader().use { it.readText() }
                        Log.d("API_RESPONSE_BODY", "Response: $response")
                    } else {
                        Log.e("API_ERROR", "Failed with Response Code: $responseCode, Message: $responseMessage")
                    }
                } catch (e: Exception) {
                    Log.e("API_EXCEPTION", "Error occurred during API request", e)
                } finally {
                    attempts++
                    Log.d("API_REQUEST", "Attempt $attempts completed")
                }
            }

            if (!success) {
                Log.e("API_FAILURE", "API request failed after $attempts attempts")
            } else {
                Log.d("API_SUCCESS", "API request succeeded after $attempts attempts")
            }
        }
    }

    private fun fetchProductDetailsAndUpdatePrices() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val url = "https://b2c-backend-eik4.onrender.com/api/v1/admin/getallproducts"
                val client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: ""
                    val jsonArray = JSONArray(responseBody)
                    // Build a map from product name (from API) to the current price
                    val productPriceMap = mutableMapOf<String, Double>()
                    for (i in 0 until jsonArray.length()) {
                        val productObj = jsonArray.getJSONObject(i)
                        val productName = productObj.getString("name") // e.g., "6pc_tray"
                        // Convert the price string to Double (fallback to getDouble)
                        val price = productObj.getString("price").toDoubleOrNull() ?: productObj.getDouble("price")
                        productPriceMap[productName] = price
                    }

                    // Update cart items: Map "Eggs x 6" -> "6pc_tray", etc.
                    cartItems.forEach { item ->
                        when (item.name) {
                            "Eggs x 6" -> productPriceMap["6pc_tray"]?.let { price ->
                                item.price = BigDecimal(price).setScale(2, RoundingMode.HALF_UP).toDouble()
                            }
                            "Eggs x 12" -> productPriceMap["12pc_tray"]?.let { price ->
                                item.price = BigDecimal(price).setScale(2, RoundingMode.HALF_UP).toDouble()
                            }
                            "Eggs x 30" -> productPriceMap["30pc_tray"]?.let { price ->
                                item.price = BigDecimal(price).setScale(2, RoundingMode.HALF_UP).toDouble()
                            }
                        }
                    }
                    // Switch to main thread to update UI
                    withContext(Dispatchers.Main) {
                        view?.let {
                            updateTotalPrice()
                            cartAdapter.notifyDataSetChanged()
                        }
                    }
                } else {
                    Log.e("ProductAPI", "Failed to fetch product details: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("ProductAPI", "Exception in fetching product details", e)
            }
        }
    }

}
