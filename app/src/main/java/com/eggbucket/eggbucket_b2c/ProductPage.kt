package com.eggbucket.eggbucket_b2c

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar


class ProductPageFragment : Fragment() {

    private val images = listOf(R.drawable.egg_basket3, R.drawable.egg_basket5, R.drawable.egg_basket)
    private lateinit var sharedPreferences: SharedPreferences
    private var quantity = 0
    private var currentPack = 0 // 0 for pack of 6, 1 for pack of 12, 2 for pack of 30
    private var count1 = 0
    private var count2 = 0
    private var count3 = 0
    private var price1 = 60
    private var price2 = 120
    private var price3 = 300

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        loadQuantitiesFromSharedPreferences()

        val view = inflater.inflate(R.layout.fragment_product_page, container, false)
        val viewPager = view.findViewById<ViewPager2>(R.id.imageCarousel)
        val carouselAdapter = CarouselAdapterProduct(images)
        viewPager.adapter = carouselAdapter

        val packOf6 = view.findViewById<View>(R.id.card1)
        val packOf12 = view.findViewById<View>(R.id.card2)
        val packOf30 = view.findViewById<View>(R.id.card3)
        val priceText = view.findViewById<TextView>(R.id.priceText)


        //fetching inc, dec and number text button and views
        val decreaseBut = view.findViewById<ImageButton>(R.id.decreaseButton)
        val increaseBut = view.findViewById<ImageButton>(R.id.increaseButton)
        val quantityText = view.findViewById<TextView>(R.id.quantityText)


        //fetching card layout ids
        val cardLayout1 = view.findViewById<LinearLayout>(R.id.cardLayout1)
        val cardLayout2 = view.findViewById<LinearLayout>(R.id.cardLayout2)
        val cardLayout3 = view.findViewById<LinearLayout>(R.id.cardLayout3)

        // Set initial quantity
        quantityText.text = quantity.toString()

        // Pack selection listeners
        packOf6.setOnClickListener {
            currentPack = 0
            quantity = count1
            quantityText.text = quantity.toString()
            priceText.text = "₹ $price1"
            //changing the current set card border to black and others to orange
            cardLayout1.setBackgroundResource(R.drawable.card_selector)
            cardLayout2.setBackgroundResource(R.drawable.card_background)
            cardLayout3.setBackgroundResource(R.drawable.card_background)
        }


        //Pack of 12 on click Listener
        packOf12.setOnClickListener {
            currentPack = 1
            quantity = count2
            quantityText.text = quantity.toString()
            priceText.text = "₹ $price2"
            cardLayout2.setBackgroundResource(R.drawable.card_selector)
            cardLayout1.setBackgroundResource(R.drawable.card_background)
            cardLayout3.setBackgroundResource(R.drawable.card_background)
        }

        //Pack of 30 on click Listener
        packOf30.setOnClickListener {
            currentPack = 2
            quantity = count3
            quantityText.text = quantity.toString()
            priceText.text = "₹ $price3"
            cardLayout3.setBackgroundResource(R.drawable.card_selector)
            cardLayout1.setBackgroundResource(R.drawable.card_background)
            cardLayout2.setBackgroundResource(R.drawable.card_background)
        }

        // Decrease quantity event listener
        decreaseBut.setOnClickListener {
            if (quantity > 0) {
                quantity--
                quantityText.text = quantity.toString()
                saveQuantityToSharedPreferences()
            }
        }

        // Increase quantity event listener
        increaseBut.setOnClickListener {
            quantity++
            quantityText.text = quantity.toString()
            saveQuantityToSharedPreferences()
        }

        //fetching previous button to go back
        val previousButton = view.findViewById<ImageButton>(R.id.backButton)
        previousButton.setOnClickListener {
            findNavController().navigate(R.id.action_productPageFragment_to_navigation_home)
        }

        val messageTextView = view.findViewById<TextView>(R.id.messageTextView)

        //fetching add to cart button to go to cart
        val addToCart = view.findViewById<Button>(R.id.addToCartButton)
        addToCart.setOnClickListener {
            // Load quantities from SharedPreferences
            loadQuantitiesFromSharedPreferences()

            // Log current counts for debugging
            Log.d("ProductPageFragment", "Count1: $count1, Count2: $count2, Count3: $count3")

            // Check if all quantities are 0
            if (count1 == 0 && count2 == 0 && count3 == 0) {
                // Set the message in the TextView and make it visible
                messageTextView.text = "Cart is Empty!"
                messageTextView.visibility = View.VISIBLE

                // Change the background color of the message to make it more visible
                messageTextView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.appThemeColor)) // Use a distinct color

                // Dismiss the message after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    messageTextView.visibility = View.GONE
                }, 2000)
            } else {
                // Navigate to the CartActivity
                findNavController().navigate(R.id.action_productPageFragment_to_cartActivity)
            }
        }



        return view
    }


    //this function maintains the count1,2,3 consistent across product page and cart page
    override fun onResume() {
        super.onResume()
        loadQuantitiesFromSharedPreferences() // Reload quantities on resume
    }

    //save quantity to shared Preferences
    private fun saveQuantityToSharedPreferences() {
        when (currentPack) {
            0 -> count1 = quantity
            1 -> count2 = quantity
            2 -> count3 = quantity
        }

        with(sharedPreferences.edit()) {
            putInt("count1", count1)
            putInt("count2", count2)
            putInt("count3", count3)
            apply()
        }
    }


    //loading quantity to shared Preferences
    private fun loadQuantitiesFromSharedPreferences() {
        count1 = sharedPreferences.getInt("count1", 0)
        count2 = sharedPreferences.getInt("count2", 0)
        count3 = sharedPreferences.getInt("count3", 0)
        quantity = when (currentPack) {
            0 -> count1
            1 -> count2
            2 -> count3
            else -> 0
        }
    }


    //printing quantities to console
//    private fun printQuantityToConsole() {
//        Log.d("ProductPageFragment", "Current Quantity: $quantity")
//    }
//
//    private fun printAllSharedPreferences() {
//        val allEntries: Map<String, *> = sharedPreferences.all
//        for ((key, value) in allEntries) {
//            Log.d("SharedPreferences", "Key: $key, Value: $value")
//        }
//    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductPageFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}




