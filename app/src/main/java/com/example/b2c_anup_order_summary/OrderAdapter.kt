package com.example.b2c_anup_order_summary


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(private val items: List<OrderItem>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageEgg: ImageView = view.findViewById(R.id.imageEgg)
        val textTitle: TextView = view.findViewById(R.id.textTitle)
        val textDate: TextView = view.findViewById(R.id.textDate)
        val textDelivered: TextView = view.findViewById(R.id.textDelivered)
        val buttonReorder: Button = view.findViewById(R.id.buttonReorder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val item = items[position]
        holder.imageEgg.setImageResource(item.imageResId)
        holder.textTitle.text = "Eggs x ${item.quantity}"
        holder.textDate.text = "Ordered on ${item.date}"
        holder.textDelivered.text = "Delivered"
        holder.buttonReorder.setOnClickListener {
            Toast.makeText(
                holder.itemView.context,
                "Reorder clicked for Eggs x ${item.quantity}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int = items.size
}
