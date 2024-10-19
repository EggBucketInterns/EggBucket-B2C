package com.eggbucket.eggbucket_b2c

import android.view.View
import androidx.activity.viewModels
import com.cartpage.app.appcomponents.base.BaseActivity
import com.cartpage.app.databinding.ActivityCartPageBinding
import com.eggbucket.eggbucket_b2c.cartpage.data.model.OrderlistRowModel
import com.cartpage.app.modules.cartpage.`data`.viewmodel.CartPageVM
import com.cartpage.app.modules.cartpage.ui.OrderlistAdapter
import kotlin.Int
import kotlin.String
import kotlin.Unit

class CartPageActivity : BaseActivity<ActivityCartPageBinding>(R.layout.activity_cart_page) {
  private val viewModel: CartPageVM by viewModels<CartPageVM>()

  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    val orderlistAdapter = OrderlistAdapter(viewModel.orderlistList.value?:mutableListOf())
    binding.recyclerOrderlist.adapter = orderlistAdapter
    orderlistAdapter.setOnItemClickListener(
    object : OrderlistAdapter.OnItemClickListener {
      override fun onItemClick(view:View, position:Int, item : OrderlistRowModel) {
        onClickRecyclerOrderlist(view, position, item)
      }
    }
    )
    viewModel.orderlistList.observe(this) {
      orderlistAdapter.updateData(it)
    }
    binding.cartPageVM = viewModel
  }

  override fun setUpClicks(): Unit {
  }

  fun onClickRecyclerOrderlist(
    view: View,
    position: Int,
    item: OrderlistRowModel
  ): Unit {
    when(view.id) {
    }
  }

  companion object {
    const val TAG: String = "CART_PAGE_ACTIVITY"

  }
}
