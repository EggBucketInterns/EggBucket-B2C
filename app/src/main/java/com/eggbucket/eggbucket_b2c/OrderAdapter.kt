package com.eggbucket.eggbucket_b2c

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(private val items: List<OrderI>) :
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
            .inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val item = items[position]

        // Set overlay image only for first 6 items (0 to 5)
        val imageResId = when (position) {
            0, 3 -> R.drawable.es_6
            1, 4 -> R.drawable.es_12
            2, 5 -> R.drawable.es_30
            else -> item.imageResId
        }

        holder.imageEgg.setImageResource(imageResId)
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
