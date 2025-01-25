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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class HomeScreen : Fragment() {

    private val images = listOf(R.drawable.freshegg, R.drawable.farmfresh, R.drawable.promo)
    private lateinit var viewPager: ViewPager2
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val view = inflater.inflate(R.layout.fragment_home_screen, container, false)
        viewPager = view.findViewById(R.id.carouselViewPager)
        val carouselAdapter = CarouselAdapter(images)
        viewPager.adapter = carouselAdapter

        val ind1 = view.findViewById<ImageView>(R.id.indicator1)
        val ind2 = view.findViewById<ImageView>(R.id.indicator2)
        val ind3 = view.findViewById<ImageView>(R.id.indicator3)

        fetchProductData(view, ind1, ind2, ind3)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
                updateIndicatorBackgrounds(ind1, ind2, ind3, currentPage)
            }
        })

        return view
    }

    private fun fetchProductData(view: View, ind1: ImageView, ind2: ImageView, ind3: ImageView) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("https://b2c-backend-1.onrender.com/api/v1/deliveryPartner/getproduct")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                if (connection.responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val productArray = JSONArray(response)
                    requireActivity().runOnUiThread {
                        setupProducts(view, productArray)
                        startAutoScroll(ind1, ind2, ind3)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupProducts(view: View, productArray: JSONArray) {
        val productMapping = mapOf(
            "6pc_tray" to R.id.itemCard1,
            "12pc_tray" to R.id.itemCard2,
            "30pc_tray" to R.id.itemCard3
        )

        for (i in 0 until productArray.length()) {
            val product = productArray.getJSONObject(i)
            val name = product.getString("name")
            val countInStock = product.getInt("countInStock")
            val cardViewId = productMapping[name] ?: continue

            val cardView = view.findViewById<CardView>(cardViewId)
            val addButton = cardView.findViewById<Button>(resources.getIdentifier("addButton${i + 1}", "id", requireContext().packageName))
            val outOfStockImage = cardView.findViewById<ImageView>(resources.getIdentifier("outOfStockImage${i + 1}", "id", requireContext().packageName))
            val counterLayout = cardView.findViewById<LinearLayout>(resources.getIdentifier("counterLayout${i + 1}", "id", requireContext().packageName))

            // If product is out of stock
            if (countInStock <= 0) {
                outOfStockImage.visibility = View.VISIBLE
                addButton.visibility = View.GONE
                counterLayout.visibility = View.GONE
                cardView.isClickable = false // Disable click
            } else {
                // If product is in stock
                outOfStockImage.visibility = View.GONE
                addButton.visibility = View.VISIBLE
                cardView.isClickable = true // Enable click
                setupCard(
                    cardView = cardView,
                    addButton = addButton,
                    counterLayout = counterLayout,
                    incrementButton = cardView.findViewById(resources.getIdentifier("incrementButton${i + 1}", "id", requireContext().packageName)),
                    decrementButton = cardView.findViewById(resources.getIdentifier("decrementButton${i + 1}", "id", requireContext().packageName)),
                    itemCountText = cardView.findViewById(resources.getIdentifier("itemCount${i + 1}", "id", requireContext().packageName)),
                    sharedPreferencesKey = "count${i + 1}",
                    editor = sharedPreferences.edit(),
                    initialCount = sharedPreferences.getInt("count${i + 1}", 0)
                )

                // Enable navigation to product page for available products
                cardView.setOnClickListener {
                    when (i) {
                        0 -> findNavController().navigate(R.id.action_navigation_home_to_productPageFragment)
                        1 -> findNavController().navigate(R.id.action_navigation_home_to_product1Fragment)
                        2 -> findNavController().navigate(R.id.action_navigation_home_to_product2Fragment)
                    }
                }
            }
        }
    }


    private fun setupCard(
        cardView: CardView,
        addButton: Button,
        counterLayout: LinearLayout,
        incrementButton: Button,
        decrementButton: Button,
        itemCountText: TextView,
        sharedPreferencesKey: String,
        editor: SharedPreferences.Editor,
        initialCount: Int
    ) {
        var count = initialCount
        val navController = findNavController()

        if (count > 0) {
            addButton.visibility = View.GONE
            counterLayout.visibility = View.VISIBLE
            itemCountText.text = count.toString()
        } else {
            addButton.visibility = View.VISIBLE
            counterLayout.visibility = View.GONE
        }

        addButton.setOnClickListener {
            addButton.visibility = View.GONE
            counterLayout.visibility = View.VISIBLE
            count = 1
            itemCountText.text = count.toString()
            editor.putInt(sharedPreferencesKey, count).apply()
        }

        incrementButton.setOnClickListener {
            count++
            itemCountText.text = count.toString()
            editor.putInt(sharedPreferencesKey, count).apply()
        }

        decrementButton.setOnClickListener {
            count--
            if (count <= 0) {
                addButton.visibility = View.VISIBLE
                counterLayout.visibility = View.GONE
                editor.putInt(sharedPreferencesKey, 0).apply()
            } else {
                itemCountText.text = count.toString()
                editor.putInt(sharedPreferencesKey, count).apply()
            }
        }
    }


    private fun startAutoScroll(ind1: ImageView, ind2: ImageView, ind3: ImageView) {
        val runnable = object : Runnable {
            override fun run() {
                if (currentPage == images.size) {
                    currentPage = 0
                }
                viewPager.setCurrentItem(currentPage, true)
                updateIndicatorBackgrounds(ind1, ind2, ind3, currentPage)
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

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeScreen()
    }
}
