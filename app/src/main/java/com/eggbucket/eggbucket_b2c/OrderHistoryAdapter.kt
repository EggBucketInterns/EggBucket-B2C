package com.eggbucket.eggbucket_b2c

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat

class OrderHistoryAdapter(private val orderList: List<OrderItem>) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val orderDate: TextView = itemView.findViewById(R.id.orderDate)
        val deliveryStatus: TextView? = itemView.findViewById(R.id.deliveryStatus)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val reorderButton: TextView = itemView.findViewById(R.id.reorderButton) // Assuming reorder is a button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        val products = order.products
        val sdf = SimpleDateFormat("dd/MM/yy")
        for (key in products.keys){
            val noOfEggs = key.substring(1)
            print("Current key: $key, noOfEggs: $noOfEggs")
            if (noOfEggs == "6") {
                holder.productImage.setImageResource(R.drawable.eggimage)
                holder.productPrice.text = "Rs. 20"
            } else if (noOfEggs == "12") {
                holder.productImage.setImageResource(R.drawable.eggimage1)
                holder.productPrice.text = "Rs. 20"
            } else {

                holder.productImage.setImageResource(R.drawable.eggimage2)
                holder.productPrice.text = "Rs. 20"
            }
            holder.productName.text = "Eggs x $noOfEggs x ${products[key]}"
            holder.orderDate.text = sdf.format(order.createdAt._seconds)
            holder.deliveryStatus?.text = order.status
        }



        holder.reorderButton.setOnClickListener {
        }
    }

    override fun getItemCount(): Int = orderList.size
}
