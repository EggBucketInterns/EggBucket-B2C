package com.eggbucket.eggbucket_b2c

import android.content.Intent
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
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2

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

        val subtractButton = view.findViewById<ImageButton>(R.id.subtractButton)
        val positiveButton = view.findViewById<ImageButton>(R.id.positiveButton)
        val quantityText = view.findViewById<TextView>(R.id.quantityText)
        val itemPrice = view.findViewById<TextView>(R.id.itemPrice)
        val addButton = view.findViewById<Button>(R.id.addButton)
        val ind1 = view.findViewById<ImageView>(R.id.indicator1)
        val ind2 = view.findViewById<ImageView>(R.id.indicator2)
        val ind3 = view.findViewById<ImageView>(R.id.indicator3)
        val itemCard = view.findViewById<CardView>(R.id.itemCard)

//        itemCard.setOnClickListener {
//                val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
//                fragmentTransaction.replace(R.id.homeFragment, ProductPageFragment())
//                fragmentTransaction.addToBackStack(null)
//                fragmentTransaction.commit()
//        }



        var quantity = 1
        quantityText.text = quantity.toString()

        var totalPrice = 8
        itemPrice.text = totalPrice.toString()


        addButton.setOnClickListener {
            addButton.visibility = View.GONE
            subtractButton.visibility = View.VISIBLE
            quantityText.visibility = View.VISIBLE
            positiveButton.visibility = View.VISIBLE
        }

        subtractButton.setOnClickListener {
            if (quantity > 1) {
                quantity--
                quantityText.text = quantity.toString()
                totalPrice -= 8
                itemPrice.text = totalPrice.toString()
            } else {
                subtractButton.visibility = View.GONE
                positiveButton.visibility = View.GONE
                quantityText.visibility = View.GONE
                addButton.visibility = View.VISIBLE
            }
        }

        positiveButton.setOnClickListener {
            quantity++
            quantityText.text = quantity.toString()
            totalPrice += 8
            itemPrice.text = totalPrice.toString()
        }

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
