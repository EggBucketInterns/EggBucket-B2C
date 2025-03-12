package com.eggbucket.eggbucket_b2c

//import com.google.firebase.timestamp

import com.google.gson.annotations.SerializedName
//import com.google.firebase.Timestamp


data class CartItem(
    val image:String,
    val name: String,
    var quantity: Int,
    var price: Double
)

data class OrderResponse(
    val size: Int,
    val orders: List<OrderItem>
)

data class OrderItem(
    val id: String,
    @SerializedName("address") val orderAddress: OrderAddress,
    val amount: Double,
    val products: Map<String, Int>,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
    val outletId: String,
    val customerId: String,
    val deliveryPartnerId: String,
    val status: String?
)

data class OrderAddress(
    val addressLine1: String,
    val addressLine2: String,
    val city: String,
    val flatNo: String,
    val area: String,
    val zipCode: String,
    val country: String,
    val state: String,
)


data class Timestamp(
    val _seconds: Long,
    val _nanoseconds: Long
)

