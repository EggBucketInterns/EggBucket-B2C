package com.cartpage.app.modules.cartpage.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cartpage.app.R
import com.cartpage.app.databinding.RowOrderlistBinding
import com.eggbucket.eggbucket_b2c.cartpage.data.model.OrderlistRowModel
import kotlin.Int
import kotlin.collections.List

class OrderlistAdapter(
  var list: List<OrderlistRowModel>
) : RecyclerView.Adapter<OrderlistAdapter.RowOrderlistVH>() {
  private var clickListener: OnItemClickListener? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowOrderlistVH {
    val view=LayoutInflater.from(parent.context).inflate(R.layout.row_orderlist,parent,false)
    return RowOrderlistVH(view)
  }

  override fun onBindViewHolder(holder: RowOrderlistVH, position: Int) {
    val orderlistRowModel = OrderlistRowModel()
    // TODO uncomment following line after integration with data source
    // val orderlistRowModel = list[position]
    holder.binding.orderlistRowModel = orderlistRowModel
  }

  override fun getItemCount(): Int = 2
  // TODO uncomment following line after integration with data source
  // return list.size

  public fun updateData(newData: List<OrderlistRowModel>) {
    list = newData
    notifyDataSetChanged()
  }

  fun setOnItemClickListener(clickListener: OnItemClickListener) {
    this.clickListener = clickListener
  }

  interface OnItemClickListener {
    fun onItemClick(
      view: View,
      position: Int,
      item: OrderlistRowModel
    ) {
    }
  }

  inner class RowOrderlistVH(
    view: View
  ) : RecyclerView.ViewHolder(view) {
    val binding: RowOrderlistBinding = RowOrderlistBinding.bind(itemView)
    init {
      binding.btnButtontext1.setOnClickListener {
        // TODO replace with value from datasource
        clickListener?.onItemClick(it, adapterPosition, OrderlistRowModel())
      }
      binding.btnButtontext2.setOnClickListener {
        // TODO replace with value from datasource
        clickListener?.onItemClick(it, adapterPosition, OrderlistRowModel())
      }
      binding.btnButtontext3.setOnClickListener {
        // TODO replace with value from datasource
        clickListener?.onItemClick(it, adapterPosition, OrderlistRowModel())
      }
      binding.btnButtontext4.setOnClickListener {
        // TODO replace with value from datasource
        clickListener?.onItemClick(it, adapterPosition, OrderlistRowModel())
      }
    }
  }
}
