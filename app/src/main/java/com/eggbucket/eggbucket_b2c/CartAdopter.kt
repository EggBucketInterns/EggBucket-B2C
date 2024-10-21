package com.eggbucket.eggbucket_b2c
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class CartAdapter(
    private val cartItems: List<CartItem>,
    private val onQuantityChanged: () -> Unit,
    private val onRemoveItem: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.productName)
        val productPrice: TextView = view.findViewById(R.id.productPrice)
        val quantity: TextView = view.findViewById(R.id.quantity)
        val increaseBtn: TextView = view.findViewById(R.id.increase_btn)
        val decreaseBtn: TextView = view.findViewById(R.id.decrease_btn)
        val cancelButton: ImageButton = view.findViewById(R.id.cancel_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.productName.text = item.name

        holder.productPrice.text = "₹${item.price}"

        holder.quantity.text = item.quantity.toString()

        // Increase button listener
        holder.increaseBtn.setOnClickListener {
            item.quantity++
            holder.quantity.text = item.quantity.toString()
            onQuantityChanged()
        }

        // Decrease button listener
        holder.decreaseBtn.setOnClickListener {
            if (item.quantity > 1) {
                item.quantity--
                holder.quantity.text = item.quantity.toString()
                onQuantityChanged()
            }
            if(item.quantity==1){
                onRemoveItem(item)
            }
        }

        // Remove item button listener
        holder.cancelButton.setOnClickListener {
            onRemoveItem(item)
        }
    }

    override fun getItemCount() = cartItems.size
}
