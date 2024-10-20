package com.eggbucket.eggbucket_b2c

import com.google.gson.annotations.SerializedName
data class CartItem(
    val name: String,
    var quantity: Int,
    val price: Double

)

data class OrderResponse(
    val size: Int,
    val orders: List<OrderItem>
)

data class OrderItem(
    val id: String,
    val createdAt: Timestamp,
    val amount: Int,
    @SerializedName("address")val orderAddress: OrderAddress,
    val outletId: String,
    val customerId: String,
    val deliveryPartnerId: String,
    val products: Map<String, Int>,
    val updatedAt: Timestamp,
    val status: String?
)

data class OrderAddress(
    val fullAddress: String,
    @SerializedName("coordinates") val orderCoordinates: OrderCoordinates
)

data class OrderCoordinates(
    val lat: Double,
    val long: Double
)

data class Timestamp(
    val _seconds: Long,
    val _nanoseconds: Int
)

