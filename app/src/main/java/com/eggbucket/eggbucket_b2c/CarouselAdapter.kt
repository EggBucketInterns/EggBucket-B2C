package com.eggbucket.eggbucket_b2c

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

// Adapter to bind images to ViewPager2
class CarouselAdapter(private val images: List<Int>) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    // ViewHolder class to represent each carousel item
    inner class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.carouselImage)
    }

    // Creates new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.carousel_item, parent, false)
        return CarouselViewHolder(view)
    }

    // Binds the images to the views (invoked by the layout manager)
    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        holder.imageView.setImageResource(images[position])
    }

    // Returns the total number of items in the data set
    override fun getItemCount(): Int {
        return images.size
    }
}
