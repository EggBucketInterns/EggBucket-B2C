package com.cartpage.app.modules.cartpage.`data`.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cartpage.app.modules.cartpage.`data`.model.CartPageModel
import com.cartpage.app.modules.cartpage.`data`.model.OrderlistRowModel
import kotlin.collections.MutableList
import org.koin.core.KoinComponent

class CartPageVM : ViewModel(), KoinComponent {
  val cartPageModel: MutableLiveData<CartPageModel> = MutableLiveData(CartPageModel())

  var navArguments: Bundle? = null

  val orderlistList: MutableLiveData<MutableList<OrderlistRowModel>> =
      MutableLiveData(mutableListOf())
}
