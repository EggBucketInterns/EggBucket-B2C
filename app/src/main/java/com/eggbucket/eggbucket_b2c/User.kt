package com.eggbucket.eggbucket_b2c

import com.google.gson.annotations.SerializedName

// Main User Data Class
data class User(
    val userId: String,        // Unique user ID
    val name: String,          // User's full name
    val email: String,         // User's email address
    val phone: String,         // User's phone number
    val addresses: List<UserAddress> // List of user addresses
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

