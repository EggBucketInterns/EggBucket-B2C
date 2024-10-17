package com.eggbucket.eggbucket_b2c

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("api/v1/order/order")
    fun getPreviousOrders(
        @Query("customerId") customerId: String,  // Query param for customerId
    ): Call<OrderResponse>                         // This returns a list of orders
}
