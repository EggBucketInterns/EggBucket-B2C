package com.eggbucket.eggbucket_b2c

import com.google.gson.annotations.SerializedName

// Main User Data Class
data class User(
    val id: String,
    val phone: String,
    val timeOfCreation: Long,
    val gender: String,
    val age: String,
    val email: String,
    val totalExpenditure: Int,
    val totalOrders: Int,
    val name: String,
    @SerializedName("addresses") val userAddresses: List<UserAddress>
)

// Nested Address Class
data class UserAddress(
    val id:String?=null,
    val fullAddress: FullAddress,
    @SerializedName("coordinates") val customerCoordinates: CustomerCoordinates
)

// Full Address Class
data class FullAddress(
    val area: String,
    val zipCode: String,
    val country: String,
    val flatNo: String,
    val city: String,
    val state: String
)

// Coordinates Class
data class CustomerCoordinates(
    val lat: Double,
    val long: Double
)

