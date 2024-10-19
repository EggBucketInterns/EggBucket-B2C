package com.eggbucket.eggbucket_b2c

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.eggbucket.eggbucket_b2c.homepage.EggItemAdapter
import com.eggbucket.eggbucket_b2c.homepage.ProductItem

class HomeScreen : Fragment() {

    private val images = listOf(R.drawable.cros_1, R.drawable.cros_2, R.drawable.cros_3)
    private lateinit var viewPager: ViewPager2
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0

    private lateinit var eggRecyclerView: RecyclerView
    private lateinit var eggItemAdapter: EggItemAdapter

    private val productList: MutableList<ProductItem> = mutableListOf(
        ProductItem("Eggs", 8, R.drawable.productimg, 0),
        ProductItem("Milk", 10, R.drawable.productimg, 0),
        ProductItem("Bread", 20, R.drawable.productimg, 0),
        ProductItem("Cheese", 30, R.drawable.productimg, 0)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_screen, container, false)

        viewPager = view.findViewById(R.id.carouselViewPager)
        viewPager.adapter = CarouselAdapter(images)

        eggRecyclerView = view.findViewById(R.id.EggRecyclerView)
        eggItemAdapter = EggItemAdapter(productList)

        // Use GridLayoutManager for 2 columns
        eggRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        eggRecyclerView.addItemDecoration(GridSpacingItemDecoration(8))
        eggRecyclerView.adapter = eggItemAdapter

        // Initialize indicators
        val ind1 = view.findViewById<ImageView>(R.id.indicator1)
        val ind2 = view.findViewById<ImageView>(R.id.indicator2)
        val ind3 = view.findViewById<ImageView>(R.id.indicator3)

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
    class GridSpacingItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: android.graphics.Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.set(spacing, spacing, spacing, spacing)
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
