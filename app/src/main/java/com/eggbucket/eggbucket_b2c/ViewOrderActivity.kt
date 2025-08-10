//package com.eggbucket.eggbucket_b2c
//
//import android.os.Bundle
//import android.view.Gravity
//import android.widget.ImageView
//import android.widget.LinearLayout
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//
//class ViewOrderActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.fragment_view_order) // ← Replace with your actual XML layout name
//
//        val buildingName = findViewById<TextView>(R.id.buildingName)
//        val locationName = findViewById<TextView>(R.id.locationName)
//        val itemsContainer = findViewById<LinearLayout>(R.id.itemsContainer)
//        val totalPrice = findViewById<TextView>(R.id.totalPrice)
//        val backButton = findViewById<ImageView>(R.id.backButton)
//
//        backButton.setOnClickListener { finish() }
//
//        val building = intent.getStringExtra("buildingName") ?: "Building name"
//        val location = intent.getStringExtra("locationName") ?: "Location Name, Area Name"
//
//        val items = intent.getSerializableExtra("orderItems") as? List<Pair<String, Int>>
//            ?: listOf(
//                "Item-1" to 100,
//                "Item-2" to 150,
//                "Item-3" to 200
//            )
//
//        buildingName.text = "   $building"
//        locationName.text = "   $location"
//
//        var total = 0
//        for ((name, price) in items) {
//            val row = LinearLayout(this).apply {
//                orientation = LinearLayout.HORIZONTAL
//                weightSum = 2f
//            }
//
//            val nameView = TextView(this).apply {
//                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
//                text = name
//                textSize = 16f
//            }
//
//            val priceView = TextView(this).apply {
//                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
//                text = "₹$price"
//                textSize = 16f
//                gravity = Gravity.END
//            }
//
//            row.addView(nameView)
//            row.addView(priceView)
//            itemsContainer.addView(row)
//
//            total += price
//        }
//
//        totalPrice.text = "Total: ₹$total"
//    }
//}

package com.eggbucket.eggbucket_b2c
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.TimeUnit
import android.util.TypedValue// <-- Added this import

class ViewOrderActivity : AppCompatActivity() {

    // UI elements are declared as lateinit properties
    private lateinit var buildingNameTextView: TextView
    private lateinit var locationNameTextView: TextView
    private lateinit var itemsContainerLayout: LinearLayout
    private lateinit var totalPriceTextView: TextView
    private lateinit var backButton: ImageView
    private lateinit var deliveryPartnerNameTextView: TextView
    private lateinit var orderNoteTextView: TextView
    private lateinit var thankYouButton: Button

    // SharedPreferences for local data storage and user phone number
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var phoneNumber: String

    // Mutable list to hold the cart items that will be displayed
    private val cartItems = mutableListOf<CartItem>()

    // Mapping from display name (used in CartItem) to backend product name (used in API response)
    // This is crucial for retrieving the correct prices.
    private val productApiNameMap = mapOf(
        "Eggs x 6" to "6pc_tray",
        "Eggs x 12" to "12pc_tray",
        "Eggs x 30" to "30pc_tray"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the content view to the layout defined in fragment_view_order.xml
        setContentView(R.layout.fragment_view_order)

        // Initialize all UI elements by finding them by their IDs
        buildingNameTextView = findViewById(R.id.buildingName)
        locationNameTextView = findViewById(R.id.locationName)
        itemsContainerLayout = findViewById(R.id.itemsContainer)
        totalPriceTextView = findViewById(R.id.totalPrice)
        backButton = findViewById(R.id.backButton)
        deliveryPartnerNameTextView = findViewById(R.id.deliveryLabel) // Corresponds to "Delivery Partner Name"
        orderNoteTextView = findViewById(R.id.orderNote) // Corresponds to "Your order is placed..."
        thankYouButton = findViewById(R.id.thankYouBtn) // The "Thank you" button

        // Initialize SharedPreferences to access locally stored data
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        // Retrieve the user's phone number, providing a default if not found
        phoneNumber = sharedPreferences.getString("user_phone", "916363894956").toString()

        // Set up click listeners for the back button and the thank you button
        backButton.setOnClickListener { finish() } // Closes the current activity, navigating back
        thankYouButton.setOnClickListener { finish() } // Also closes the activity, acting as a "done" button

        // Populate the cart items list from locally saved quantities in SharedPreferences
        populateCartItemsFromSharedPreferences()

        // Asynchronously fetch the latest product prices from the backend
        // This is important because the prices stored in SharedPreferences might be outdated.
        fetchProductDetailsAndUpdatePrices()

        // Set static text for delivery partner name (can be made dynamic if data is available)
        deliveryPartnerNameTextView.text = "Our Delivery Partner"

        // The order note is already set in the XML, no dynamic update here unless needed
        // orderNoteTextView.text = "Your order is confirmed.\n\nNote: Payment is accepted only in COD mode"

        // Perform the initial UI update using data from SharedPreferences (prices will be 0.0 initially)
        // This will be re-run with correct prices after fetchProductDetailsAndUpdatePrices completes.
        updateAddressAndOrderSummaryUI()
    }

    /**
     * Reconstructs the cart items list (`cartItems`) based on product quantities
     * stored in SharedPreferences (`count1`, `count2`, `count3`).
     * Initial prices are set to 0.0 and will be updated by `fetchProductDetailsAndUpdatePrices`.
     */
    private fun populateCartItemsFromSharedPreferences() {
        cartItems.clear() // Clear any existing items in the list to avoid duplication

        // Retrieve quantities for each product type from SharedPreferences
        val count1 = sharedPreferences.getInt("count1", 0) // Quantity for "Eggs x 6"
        val count2 = sharedPreferences.getInt("count2", 0) // Quantity for "Eggs x 12"
        val count3 = sharedPreferences.getInt("count3", 0) // Quantity for "Eggs x 30"

        // Add CartItem objects to the list only if their quantity is greater than zero
        if (count1 > 0) cartItems.add(CartItem("image6", "Eggs x 6", count1, 0.0))
        if (count2 > 0) cartItems.add(CartItem("image12", "Eggs x 12", count2, 0.0))
        if (count3 > 0) cartItems.add(CartItem("image30", "Eggs x 30", count3, 0.0))

        Log.d("ViewOrderActivity", "Cart items reconstructed from SharedPreferences: $cartItems")
    }

    /**
     * Fetches the latest product details, specifically prices, from the backend API.
     * After successful retrieval, it updates the prices of the `CartItem` objects
     * in `cartItems` and then triggers a UI update.
     */
    private fun fetchProductDetailsAndUpdatePrices() {
        // Launch a coroutine in the IO dispatcher for network operations
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Define the API URL for fetching all products
                val url = "https://b2c-backend-eik4.onrender.com/api/v1/admin/getallproducts"
                // Configure OkHttpClient with timeouts for robust network requests
                val client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build()
                // Build the HTTP GET request
                val request = Request.Builder().url(url).build()
                // Execute the request synchronously (within the coroutine's background thread)
                val response = client.newCall(request).execute()

                // Check if the API response was successful (HTTP 2xx)
                if (response.isSuccessful) {
                    // Read the response body as a string, default to empty if null
                    val responseBody = response.body?.string() ?: ""
                    // Parse the response body as a JSON array
                    val jsonArray = JSONArray(responseBody)

                    // Create a map to store product names (from backend) to their prices
                    val productPriceMap = mutableMapOf<String, Double>()
                    // Iterate through the JSON array to populate the price map
                    for (i in 0 until jsonArray.length()) {
                        val productObj = jsonArray.getJSONObject(i)
                        val productName = productObj.getString("name") // e.g., "6pc_tray"
                        // Get the price, handling cases where it might be a string or double
                        val price = productObj.getString("price").toDoubleOrNull() ?: productObj.getDouble("price")
                        productPriceMap[productName] = price
                    }

                    // Update the prices of CartItem objects in our `cartItems` list
                    cartItems.forEach { item ->
                        // Get the backend product name using the display name (e.g., "Eggs x 6" -> "6pc_tray")
                        productApiNameMap[item.name]?.let { apiName ->
                            // Find the corresponding price from the fetched `productPriceMap`
                            productPriceMap[apiName]?.let { price ->
                                // Update item's price, applying BigDecimal for precise rounding
                                item.price = BigDecimal(price).setScale(2, RoundingMode.HALF_UP).toDouble()
                            }
                        }
                    }

                    // Switch back to the Main (UI) thread to update the user interface
                    withContext(Dispatchers.Main) {
                        updateAddressAndOrderSummaryUI() // Re-render UI with updated prices
                    }
                } else {
                    // Log error if API call was not successful
                    Log.e("ViewOrderActivity", "Failed to fetch product details: ${response.message}")
                    withContext(Dispatchers.Main) {
                        // Show a Toast message to the user about the failure
                        Toast.makeText(this@ViewOrderActivity, "Failed to get product prices.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // Log any exceptions that occur during the network request
                Log.e("ViewOrderActivity", "Exception in fetching product details", e)
                withContext(Dispatchers.Main) {
                    // Show a Toast message for network-related errors
                    Toast.makeText(this@ViewOrderActivity, "Network error fetching product prices.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Updates the UI elements for the address display and the order summary.
     * This function is called both initially and after product prices are updated.
     */
    private fun updateAddressAndOrderSummaryUI() {
        // --- Display Address Details ---
        // Retrieve the saved address JSON string from SharedPreferences
        val addressJson = sharedPreferences.getString("selected_address", null)
        if (addressJson != null) {
            try {
                // Parse the JSON string into a UserAddress object using Gson
                val userAddress = Gson().fromJson(addressJson, UserAddress::class.java)
                // Set the building name (assuming flatNo corresponds to it)
                buildingNameTextView.text = "   ${userAddress.fullAddress.flatNo}"
                // Set the location name (area and city)
                locationNameTextView.text = "   ${userAddress.fullAddress.area}, ${userAddress.fullAddress.city}"
            } catch (e: Exception) {
                // Log and show error if parsing the address JSON fails
                Log.e("ViewOrderActivity", "Error parsing saved address JSON", e)
                buildingNameTextView.text = "   Address data error"
                locationNameTextView.text = "   Please re-select address"
            }
        } else {
            // Display default messages if no address is found in SharedPreferences
            buildingNameTextView.text = "   No address selected"
            locationNameTextView.text = "   Please select address from cart"
        }

        // --- Display Order Summary Items ---
        var totalOrderAmount = 0.0 // Initialize total amount
        itemsContainerLayout.removeAllViews() // Clear any dynamically added views to prevent duplication on update

        // If there are items in the cart, populate the summary section
        if (cartItems.isNotEmpty()) {
            for (item in cartItems) {
                // Calculate the total price for the current item (quantity * price)
                val individualItemTotal = item.quantity * item.price
                totalOrderAmount += individualItemTotal // Add to overall total

                // Create a new horizontal LinearLayout for each item row (e.g., "Eggs x 6 (x2)   ₹500.00")
                val rowLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 4, 0, 4) // Add vertical spacing between item rows
                    }
                }

                // TextView for item name and quantity (e.g., "Eggs x 6 (x2)")
                val nameQuantityTextView = TextView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        0, // Width is 0dp, allowing weight to distribute space
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f // Takes 1 part of the available width
                    )
                    text = "${item.name} (x${item.quantity})" // Display name and quantity
                    // Correct way to set text size in SP in Kotlin code
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f) // Changed: textSize = 16sp -> setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    setTextColor(ContextCompat.getColor(context, R.color.black)) // Set text color from resources
                }

                // TextView for individual item's total price
                val itemPriceTextView = TextView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        0, // Width is 0dp
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f // Takes 1 part of the available width
                    )
                    // Format the price to two decimal places
                    text = "₹${String.format("%.2f", individualItemTotal)}"
                    // Correct way to set text size in SP in Kotlin code
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f) // Changed: textSize = 16sp -> setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                    gravity = Gravity.END // Align price to the right of the row
                }

                // Add the TextViews to the current item's row layout
                rowLayout.addView(nameQuantityTextView)
                rowLayout.addView(itemPriceTextView)
                // Add the completed row layout to the main items container
                itemsContainerLayout.addView(rowLayout)
            }
        } else {
            // If `cartItems` is empty, display a message indicating no items
            val noItemsTextView = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                text = "No items found in order summary."
                // Correct way to set text size in SP in Kotlin code
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f) // Changed: textSize = 16sp -> setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                gravity = Gravity.CENTER
                setPadding(0, 16, 0, 0)
            }
            itemsContainerLayout.removeAllViews() // Ensure container is empty
            itemsContainerLayout.addView(noItemsTextView)
        }

        // --- Display Total Price ---
        // Set the final calculated total order amount, formatted to two decimal places
        totalPriceTextView.text = "Total: ₹${String.format("%.2f", totalOrderAmount)}"
    }
}
