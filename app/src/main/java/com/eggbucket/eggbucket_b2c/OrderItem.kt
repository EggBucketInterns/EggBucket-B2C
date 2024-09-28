package com.eggbucket.eggbucket_b2c

data class OrderItem(
    val imageResId: Int,
    val productName: String,
    val orderDate: String,
    val deliveryStatus: String,
    val price: String
)
