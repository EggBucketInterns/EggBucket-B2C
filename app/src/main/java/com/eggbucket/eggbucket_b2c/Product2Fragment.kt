package com.eggbucket.eggbucket_b2c

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar

class Product2Fragment : Fragment() {

    private val images = listOf(R.drawable.eggs_image_30, R.drawable.egg_30_back, R.drawable.egg_30_open)
    private lateinit var sharedPreferences: SharedPreferences

    private var count3 = 0
    private val price3 = 300 // Fixed price for pack of 30
    private lateinit var viewPager: ViewPager2
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        loadQuantityFromSharedPreferences()

        val view = inflater.inflate(R.layout.fragment_product2, container, false)
        viewPager = view.findViewById(R.id.imageCarousel)
        val carouselAdapter = CarouselAdapterProduct(images)
        viewPager.adapter = carouselAdapter

        setupCard3Listeners(view)
        setupUI(view)
        startAutoScroll(view)

        return view
    }

    private fun setupCard3Listeners(view: View) {
        val decreaseBut3 = view.findViewById<ImageButton>(R.id.decreaseButton3)
        val increaseBut3 = view.findViewById<ImageButton>(R.id.increaseButton3)
        val quantityText3 = view.findViewById<TextView>(R.id.quantityText3)

        decreaseBut3.setOnClickListener {
            if (count3 > 0) {
                count3--
                quantityText3.text = count3.toString()
                updatePrice(view)
                saveQuantityToSharedPreferences()
            }
        }

        increaseBut3.setOnClickListener {
            count3++
            quantityText3.text = count3.toString()
            updatePrice(view)
            saveQuantityToSharedPreferences()
        }
    }

    private fun updatePrice(view: View) {
        val priceTextView = view.findViewById<TextView>(R.id.priceText)
        val totalPrice = price3 * count3
        priceTextView.text = "Price: $totalPrice"
    }

    private fun saveQuantityToSharedPreferences() {
        with(sharedPreferences.edit()) {
            putInt("count3", count3)
            apply()
        }
    }

    private fun loadQuantityFromSharedPreferences() {
        count3 = sharedPreferences.getInt("count3", 0)
    }

    private fun startAutoScroll(view: View) {
        val indi1 = view.findViewById<ImageView>(R.id.indi1)
        val indi2 = view.findViewById<ImageView>(R.id.indi2)
        val indi3 = view.findViewById<ImageView>(R.id.indi3)

        val runnable = object : Runnable {
            override fun run() {
                if (currentPage == images.size) {
                    currentPage = 0
                }
                viewPager.setCurrentItem(currentPage, true)
                updateIndicatorBackgrounds(indi1, indi2, indi3, currentPage)
                currentPage++
                handler.postDelayed(this, 5000)
            }
        }
        handler.postDelayed(runnable, 5000)
    }

    private fun updateIndicatorBackgrounds(ind1: ImageView, ind2: ImageView, ind3: ImageView, currentPage: Int) {
        ind1.setBackgroundResource(R.drawable.indicator_inactive)
        ind2.setBackgroundResource(R.drawable.indicator_inactive)
        ind3.setBackgroundResource(R.drawable.indicator_inactive)

        when (currentPage) {
            0 -> ind1.setBackgroundResource(R.drawable.indicator_active)
            1 -> ind2.setBackgroundResource(R.drawable.indicator_active)
            2 -> ind3.setBackgroundResource(R.drawable.indicator_active)
        }
    }

    override fun onResume() {
        super.onResume()
        loadQuantityFromSharedPreferences()
        val quantityText2 = view?.findViewById<TextView>(R.id.quantityText3)
        quantityText2?.text = count3.toString()
        updatePrice(requireView())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }

    private fun setupUI(view: View) {
        val previousButton = view.findViewById<ImageButton>(R.id.backButton)
        previousButton.setOnClickListener {
            findNavController().navigate(R.id.action_product2Fragment_to_navigation_home)
        }

        val addToCart = view.findViewById<Button>(R.id.addToCartButton)
        addToCart.setOnClickListener {
            if (count3 == 0) {
                Snackbar.make(view, "Add Items to Cart!", Snackbar.LENGTH_SHORT).show()
            } else {
                findNavController().navigate(R.id.action_product2Fragment_to_cartFragment)
            }
        }
    }
}
