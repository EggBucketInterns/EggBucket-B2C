package com.eggbucket.eggbucket_b2c

data class OrderResponse(
    val size: Int,
    val orders: List<OrderItem>
)

data class OrderItem(
    val id: String,
    val createdAt: Timestamp,
    val amount: Int,
    val address: Address,
    val outletId: String,
    val customerId: String,
    val deliveryPartnerId: String,
    val products: Map<String, Int>,
    val updatedAt: Timestamp,
    val status: String?
)

data class Address(
    val fullAddress: String,
    val coordinates: Coordinates
)

data class Coordinates(
    val lat: Double,
    val long: Double
)

data class Timestamp(
    val _seconds: Long,
    val _nanoseconds: Int
)

