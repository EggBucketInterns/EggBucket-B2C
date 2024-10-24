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
    private val onAddAddress: (String) -> Unit
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val buildingAddress: TextView = itemView.findViewById(R.id.buyAgainBookName)
        val fullAddress: TextView = itemView.findViewById(R.id.buyAgainBookPrice)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete_button)
        val addAddressButton: LinearLayout = itemView.findViewById(R.id.add_Address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_address_list, parent, false)
        return AddressViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = addresses[position]

        // Ensure all fields are valid before setting the text to avoid NumberFormatException
        val flatNo = address.fullAddress.flatNo ?: "N/A"   // Handle null flatNo
        val area = address.fullAddress.area ?: "N/A"       // Handle null area
        val city = address.fullAddress.city ?: "N/A"       // Handle null city
        val zipCode = address.fullAddress.zipCode ?: "00000"  // Handle null or invalid zipCode

        holder.buildingAddress.text = flatNo
        holder.fullAddress.text = "$area, $city - $zipCode"

        // Set the delete button click listener
        holder.deleteButton.setOnClickListener {
            Log.d("AddressAdapter", "Delete clicked for position: $position")
            onDeleteClick(position)
        }

        // Set the add address button click listener
        holder.addAddressButton.setOnClickListener {
            onAddAddress("$flatNo, $area, $city - $zipCode")
        }
    }

    override fun getItemCount(): Int = addresses.size
}
