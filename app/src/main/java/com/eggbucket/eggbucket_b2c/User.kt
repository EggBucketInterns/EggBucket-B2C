package com.eggbucket.eggbucket_b2c



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
    val coordinates:GeoPoint,
)

// Full Address Class
data class FullAddress(
    val flatNo: String? = null,
    val addressLine1: String? = null,
    val addressLine2: String? = null,
    val area: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zipCode: String? = null,
    val country: String? = null
)


data class GeoPoint(
    val lat: Double,
    val long: Double
)
