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
        val orderDate: TextView = itemView.findViewById(R.id.orderDate)
        val orderAmt: TextView = itemView.findViewById(R.id.orderAmt)
        val items: TextView = itemView.findViewById(R.id.items)
        val deliveryStatus: TextView = itemView.findViewById(R.id.orderId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        holder.orderDate.text = "Order At: " + sdf.format(Date((order.createdAt._seconds * 1000) + (order.createdAt._nanoseconds / 1000000)))
        holder.orderAmt.text = "Order Amount: ₹${order.amount}"
        var itemString = ""
        order.products.forEach{(key, value) -> itemString += "Eggs x ${key.substring(1)}: ${value}\n"}
        holder.items.text = itemString.trimEnd('\n')
        holder.deliveryStatus.text = "Order ID: ${order.id}\nStatus: ${order.status}"
    }

    override fun getItemCount(): Int = orderList.size
}
