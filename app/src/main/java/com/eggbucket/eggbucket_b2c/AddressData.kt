package com.eggbucket.eggbucket_b2c

data class User(
    val userId: String,        // Unique user ID
    val name: String,          // User's full name
    val email: String,         // User's email address
    val phone: String,         // User's phone number
    val addresses: List<UserAddress> // List of user addresses
)
//data class UserAddress(
//    val id: String,            // Unique ID of the address
//    val fullAddress: FullAddress,
//    val coordinates: Coordinates// Nested object with full address details
//)
data class FullAddress(
    var flatNo: String?,
    var addressLine1: String?, // For user input
    val addressLine2: String?, // To be fetched from API
    val area: String?,
    val city: String?,
    val state: String?,
    val zipCode: String?,
    val country: String?
)
//data class Coordinates(
//    val lat: Double,
//    val long: Double
//)
data class UpdateAddressByIndexRequest(
    val index: Int,
    val fullAddress: FullAddress
)
data class UserAddress(
    val id:String?=null,
    val fullAddress: FullAddress,
    val coordinates:GeoPoint,
)

data class GeoPoint(
    val lat: Double,
    val long: Double
)