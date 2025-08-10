package com.eggbucket.eggbucket_b2c

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AddressAdapter(
    private var addresses: List<UserAddress>,
    private val onDeleteClick: (Int) -> Unit,
    private val onAddAddress: (UserAddress) -> Unit,
    private val onEditClick: (UserAddress,Int) -> Unit
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val buildingAddress: TextView = itemView.findViewById(R.id.buyAgainBookName)
        val fullAddress: TextView = itemView.findViewById(R.id.buyAgainBookPrice)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete_button)
        val addAddressButton: LinearLayout = itemView.findViewById(R.id.add_Address)
        val editButton: ImageView = itemView.findViewById(R.id.edit_address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_address_list, parent, false)
        return AddressViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        var address = addresses[position]

        holder.buildingAddress.text = address.fullAddress.flatNo ?: "N/A"
        holder.fullAddress.text = "${address.fullAddress.area}, ${address.fullAddress.city} - ${address.fullAddress.zipCode}"

        holder.deleteButton.setOnClickListener {
            Log.d("AddressAdapter", "Delete clicked for position: $position")
            onDeleteClick(position)
        }

        holder.addAddressButton.setOnClickListener {
            onAddAddress(address)
        }
        holder.editButton.setOnClickListener {
            onEditClick(addresses[position],position)
        }
    }

    override fun getItemCount(): Int = addresses.size
}

