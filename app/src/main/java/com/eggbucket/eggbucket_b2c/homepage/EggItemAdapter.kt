package com.eggbucket.eggbucket_b2c.homepage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eggbucket.eggbucket_b2c.R
import com.google.android.material.button.MaterialButton


data class ProductItem(
    val name: String,
    val price: Int,
    val imageRes: Int,
    var quantity: Int = 0
)

class EggItemAdapter(private val items: MutableList<ProductItem>) :
    RecyclerView.Adapter<EggItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemImage: ImageView = view.findViewById(R.id.itemImage)
        val itemName: TextView = view.findViewById(R.id.itemName)
        val itemPrice: TextView = view.findViewById(R.id.itemPrice)
        val addButton: MaterialButton = view.findViewById(R.id.addButton)
        val subtractButton: ImageButton = view.findViewById(R.id.subtractButton)
        val positiveButton: ImageButton = view.findViewById(R.id.positiveButton)
        val quantityText: TextView = view.findViewById(R.id.quantityText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.egg_item_card, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = items[position]

        holder.itemName.text = currentItem.name
        holder.itemImage.setImageResource(currentItem.imageRes)
        holder.itemPrice.text = "₹ ${currentItem.price * currentItem.quantity}"
        holder.quantityText.text = currentItem.quantity.toString()

        if (currentItem.quantity > 0) {
            holder.addButton.visibility = View.GONE
            holder.subtractButton.visibility = View.VISIBLE
            holder.quantityText.visibility = View.VISIBLE
            holder.positiveButton.visibility = View.VISIBLE
        } else {
            holder.addButton.visibility = View.VISIBLE
            holder.subtractButton.visibility = View.GONE
            holder.quantityText.visibility = View.GONE
            holder.positiveButton.visibility = View.GONE
        }

        holder.addButton.setOnClickListener {
            currentItem.quantity = 1
            notifyItemChanged(position)
        }

        holder.subtractButton.setOnClickListener {
            if (currentItem.quantity > 1) {
                currentItem.quantity--
                holder.quantityText.text = currentItem.quantity.toString()
                holder.itemPrice.text = "₹ ${currentItem.price * currentItem.quantity}"
            } else {
                currentItem.quantity = 0
                notifyItemChanged(position)
            }
        }

        holder.positiveButton.setOnClickListener {
            currentItem.quantity++
            holder.quantityText.text = currentItem.quantity.toString()
            holder.itemPrice.text = "₹ ${currentItem.price * currentItem.quantity}"
        }
    }

    override fun getItemCount(): Int = items.size
}
