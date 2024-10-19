package com.eggbucket.eggbucket_b2c

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class AddressAdapter(private var addresses: List<UserAddress>) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    // ViewHolder class to bind data
    class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val buildingAddress: TextView = itemView.findViewById(R.id.buyAgainBookName)
        val fullAddress: TextView = itemView.findViewById(R.id.buyAgainBookPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_address_list, parent, false)
        return AddressViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = addresses[position]
        holder.buildingAddress.text = address.fullAddress.flatNo
        holder.fullAddress.text = "${address.fullAddress.area}, ${address.fullAddress.city}-${address.fullAddress.zipCode}"
    }

    override fun getItemCount(): Int = addresses.size

    // Function to update the list

}