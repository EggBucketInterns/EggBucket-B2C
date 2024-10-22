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
    private val onaddAddress: (String)->Unit

) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val buildingAddress: TextView = itemView.findViewById(R.id.buyAgainBookName)
        val fullAddress: TextView = itemView.findViewById(R.id.buyAgainBookPrice)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete_button)
        val addAddressbutton: LinearLayout =itemView.findViewById(R.id.add_Address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_address_list, parent, false)
        return AddressViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = addresses[position]
        holder.buildingAddress.text = address.fullAddress.flatNo
        holder.fullAddress.text = "${address.fullAddress.area}, ${address.fullAddress.city} - ${address.fullAddress.zipCode}"

        // Set the delete button click listener
        holder.deleteButton.setOnClickListener {
            Log.d("AddressAdapter", "Delete clicked for position: $position")
            onDeleteClick(position)  // Ensure this is uncommented
        }

        holder.addAddressbutton.setOnClickListener {
            onaddAddress("${address.fullAddress.flatNo}, ${address.fullAddress.area}, ${address.fullAddress.city} - ${address.fullAddress.zipCode}")
        }
    }


    override fun getItemCount(): Int = addresses.size
}