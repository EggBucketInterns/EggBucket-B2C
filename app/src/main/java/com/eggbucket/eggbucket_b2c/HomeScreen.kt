package com.eggbucket.eggbucket_b2c

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2

class HomeScreen : Fragment() {

    private val images = listOf(R.drawable.cros_2, R.drawable.orangeyolk, R.drawable.farmfresh)
    private lateinit var viewPager: ViewPager2
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private lateinit var sharedPreferences: SharedPreferences
    private var count1:Int = 0
    private var count2:Int = 0
    private var count3:Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        // Initialize SharedPreferences and phone number

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        count1= sharedPreferences.getInt("count1", 0)
        count2= sharedPreferences.getInt("count2", 0)
        count3= sharedPreferences.getInt("count3", 0)
        val view = inflater.inflate(R.layout.fragment_home_screen, container, false)
        val editor = sharedPreferences.edit()
        viewPager = view.findViewById(R.id.carouselViewPager)
        val carouselAdapter = CarouselAdapter(images)
        viewPager.adapter = carouselAdapter
        //image indicator
        val ind1 = view.findViewById<ImageView>(R.id.indicator1)
        val ind2 = view.findViewById<ImageView>(R.id.indicator2)
        val ind3 = view.findViewById<ImageView>(R.id.indicator3)

        //setup card 1
        val itemCard1 = view.findViewById<CardView>(R.id.itemCard1)
        itemCard1.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_productPageFragment)
        }
        setupCard(
            cardView = view.findViewById(R.id.itemCard1),
            addButton = view.findViewById(R.id.addButton1),
            counterLayout = view.findViewById(R.id.counterLayout1),
            incrementButton = view.findViewById(R.id.incrementButton1),
            decrementButton = view.findViewById(R.id.decrementButton1),
            itemCountText = view.findViewById(R.id.itemCount1),
            sharedPreferencesKey = "count1",
            editor = editor,
            initialCount = count1
        )
        //card 2
        val itemCard2 = view.findViewById<CardView>(R.id.itemCard2)
        itemCard2.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_product1Fragment)
        }

        setupCard(
            cardView = view.findViewById(R.id.itemCard2),
            addButton = view.findViewById(R.id.addButton2),
            counterLayout = view.findViewById(R.id.counterLayout2),
            incrementButton = view.findViewById(R.id.incrementButton2),
            decrementButton = view.findViewById(R.id.decrementButton2),
            itemCountText = view.findViewById(R.id.itemCount2),
            sharedPreferencesKey = "count2",
            editor = editor,
            initialCount = count2
        )
        //card3
        val itemCard3 = view.findViewById<CardView>(R.id.itemCard3)
        itemCard3.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_product2Fragment)
        }
        setupCard(
            cardView = view.findViewById(R.id.itemCard3),
            addButton = view.findViewById(R.id.addButton3),
            counterLayout = view.findViewById(R.id.counterLayout3),
            incrementButton = view.findViewById(R.id.incrementButton3),
            decrementButton = view.findViewById(R.id.decrementButton3),
            itemCountText = view.findViewById(R.id.itemCount3),
            sharedPreferencesKey = "count3",
            editor = editor,
            initialCount = count3
        )
        startAutoScroll(ind1, ind2, ind3)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
                updateIndicatorBackgrounds(ind1, ind2, ind3, currentPage)
            }
        })

        return view
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

        // Set initial visibility
        if (count > 0) {
            addButton.visibility = View.GONE
            counterLayout.visibility = View.VISIBLE
            itemCountText.text = count.toString()
        } else {
            addButton.visibility = View.VISIBLE
            counterLayout.visibility = View.GONE
        }

        // Handle Add button click
        addButton.setOnClickListener {
            addButton.visibility = View.GONE
            counterLayout.visibility = View.VISIBLE
            count = 1
            itemCountText.text = count.toString()
            editor.putInt(sharedPreferencesKey, count).apply()
        }

        // Handle Increment button click
        incrementButton.setOnClickListener {
            count++
            itemCountText.text = count.toString()
            editor.putInt(sharedPreferencesKey, count).apply()
        }

        // Handle Decrement button click
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
