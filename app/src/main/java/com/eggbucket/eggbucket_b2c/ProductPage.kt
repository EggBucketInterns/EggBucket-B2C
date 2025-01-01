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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar

class ProductPageFragment : Fragment() {

    private val images = listOf(R.drawable.six_piece_eggs, R.drawable.eggside, R.drawable.orangeyolk)
    private lateinit var sharedPreferences: SharedPreferences

    private var count1 = 0
    private val price1 = 60 // Fixed Prices for each pack
    private lateinit var viewPager: ViewPager2
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        loadQuantitiesFromSharedPreferences()

        val view = inflater.inflate(R.layout.fragment_product_page, container, false)
        viewPager = view.findViewById(R.id.imageCarousel)
        val carouselAdapter = CarouselAdapterProduct(images)
        viewPager.adapter = carouselAdapter

        setupCard1Listeners(view)
        setupUI(view)
        startAutoScroll(view)

        return view
    }

    private fun setupCard1Listeners(view: View) {
        val decreaseBut1 = view.findViewById<ImageButton>(R.id.decreaseButton1)
        val increaseBut1 = view.findViewById<ImageButton>(R.id.increaseButton1)
        val quantityText1 = view.findViewById<TextView>(R.id.quantityText1)

        decreaseBut1.setOnClickListener {
            if (count1 > 0) {
                count1--
                quantityText1.text = count1.toString()
                updatePrice(view)
                saveQuantityToSharedPreferences()
            }
        }

        increaseBut1.setOnClickListener {
            count1++
            quantityText1.text = count1.toString()
            updatePrice(view)
            saveQuantityToSharedPreferences()
        }

        view.findViewById<View>(R.id.card1).setOnClickListener {
            updateSelectedCard(view)
        }
    }

    private fun updateSelectedCard(view: View) {
        view.findViewById<LinearLayout>(R.id.cardLayout1)
    }

    private fun updatePrice(view: View) {
        val priceTextView = view.findViewById<TextView>(R.id.priceText)
        priceTextView.text = "Price: ${price1 * count1}"
    }

    private fun saveQuantityToSharedPreferences() {
        with(sharedPreferences.edit()) {
            putInt("count1", count1)
            apply()
        }
    }

    private fun loadQuantitiesFromSharedPreferences() {
        count1 = sharedPreferences.getInt("count1", 0)
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
        loadQuantitiesFromSharedPreferences()
        val quantityText2 = view?.findViewById<TextView>(R.id.quantityText1)
        quantityText2?.text = count1.toString()
        updatePrice(requireView())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }


    private fun setupUI(view: View) {
        val previousButton = view.findViewById<ImageButton>(R.id.backButton)
        previousButton.setOnClickListener {
            findNavController().navigate(R.id.action_productPageFragment_to_navigation_home)
        }

        val addToCart = view.findViewById<Button>(R.id.addToCartButton)
        addToCart.setOnClickListener {
            if (count1 == 0) {
                Snackbar.make(view, "Add Items to Cart!", Snackbar.LENGTH_SHORT).show()
            } else {
                findNavController().navigate(R.id.action_productPageFragment_to_cartFragment)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductPageFragment().apply {
                arguments = Bundle().apply { }
            }
    }
}
