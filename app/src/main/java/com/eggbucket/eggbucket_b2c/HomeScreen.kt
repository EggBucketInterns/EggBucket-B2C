package com.eggbucket.eggbucket_b2c

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

class HomeScreen : Fragment() {

    private val images = listOf(R.drawable.cros_1, R.drawable.cros_2, R.drawable.cros_3)
    private lateinit var viewPager: ViewPager2
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_screen, container, false)

        viewPager = view.findViewById(R.id.carouselViewPager)
        val carouselAdapter = CarouselAdapter(images)
        viewPager.adapter = carouselAdapter
        val ind1 = view.findViewById<ImageView>(R.id.indicator1)
        val ind2 = view.findViewById<ImageView>(R.id.indicator2)
        val ind3 = view.findViewById<ImageView>(R.id.indicator3)
        val itemCard = view.findViewById<CardView>(R.id.itemCard1)
        val addButton1 = view.findViewById<Button>(R.id.addButton1)
        val addButton2 = view.findViewById<Button>(R.id.addButton2)
        val addButton3 = view.findViewById<Button>(R.id.addButton3)

        itemCard.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_productPageFragment)
        }

        addButton1.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_productPageFragment)
        }

        addButton2.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_product1Fragment)
        }

        addButton3.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_product2Fragment)
        }

        startAutoScroll(ind1, ind2, ind3)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
                updateIndicatorBackgrounds(ind1, ind2, ind3, currentPage)
            }
        })

        makeApiRequestWithRetries()

        return view
    }

    private fun makeApiRequestWithRetries() {
        CoroutineScope(Dispatchers.IO).launch {
            val url = "https://b2c-backend-1.onrender.com/api/v1/customer/user/916363894956"
            var attempts = 0
            var success = false

            while (attempts < 3 && !success) {
                try {
                    val connection = URL(url).openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"

                    val responseCode = connection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        success = true
                        val response = connection.inputStream.bufferedReader().use { it.readText() }
                        Log.d("API_RESPONSE", response)
                    } else {
                        Log.e("API_ERROR", "Response code: $responseCode")
                    }
                } catch (e: Exception) {
                    Log.e("API_ERROR", "Exception: ${e.message}")
                } finally {
                    attempts++
                }
            }

            if (!success) {
                Log.e("API_ERROR", "API request failed after 3 attempts.")
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
