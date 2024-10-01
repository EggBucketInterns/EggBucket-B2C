package com.eggbucket.eggbucket_b2c

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderHistoryAdapter(private val orderList: List<OrderItem>) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val orderDate: TextView = itemView.findViewById(R.id.orderDate)
        val deliveryStatus: TextView = itemView.findViewById(R.id.deliveryStatus)
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
        holder.productImage.setImageResource(order.imageResId)
        holder.productName.text = order.productName
        holder.orderDate.text = order.orderDate
        holder.deliveryStatus.text = order.deliveryStatus
        holder.productPrice.text = order.price

        holder.reorderButton.setOnClickListener {
        }
    }

    override fun getItemCount(): Int = orderList.size
}
