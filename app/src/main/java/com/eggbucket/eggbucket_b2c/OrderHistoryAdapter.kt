package com.eggbucket.eggbucket_b2c

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date


class OrderHistoryAdapter(private val orderList: List<OrderItem>) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val orderDate: TextView = itemView.findViewById(R.id.orderDate)
        val deliveryStatus: TextView? = itemView.findViewById(R.id.deliveryStatus)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        val products = order.products
        val sdf = SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.getDefault())
        for (key in products.keys){
            val noOfEggs = key.substring(1)
            print("Current key: $key, noOfEggs: $noOfEggs")
            if (noOfEggs == "6") {
                holder.productImage.setImageResource(R.drawable.eggs_image_6)
            } else if (noOfEggs == "12") {
                holder.productImage.setImageResource(R.drawable.eggs_image_12)
            } else {
                holder.productImage.setImageResource(R.drawable.eggs_image_30)
            }
            holder.productName.text = "Eggs x $noOfEggs x ${products[key]}"
            // println("Date of object: ${order.createdAt}")
            holder.productPrice.text = "Rs. ${order.amount}"
            holder.orderDate.text = sdf.format(Date((order.createdAt._seconds * 1000) + (order.createdAt._nanoseconds / 1000000)))
            // holder.deliveryStatus?.text = "Status: " + order.status
        }

    }

    override fun getItemCount(): Int = orderList.size
}
