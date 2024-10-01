package com.eggbucket.eggbucket_b2c

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2


class ProductPageFragment : Fragment() {

    private val images = listOf(R.drawable.cros_1, R.drawable.cros_2, R.drawable.cros_3)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_product_page, container, false)

        val viewPager = view.findViewById<ViewPager2>(R.id.imageCarousel)
        val carouselAdapter = CarouselAdapterProduct(images)
        viewPager.adapter = carouselAdapter

//        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.pageMargin)
//        val offsetPx = resources.getDimensionPixelOffset(R.dimen.offset)
//        viewPager.setPageTransformer { page, position ->
//            val offset = position * -(2 * offsetPx + pageMarginPx)
//            page.translationX = offset
//        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductPageFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}