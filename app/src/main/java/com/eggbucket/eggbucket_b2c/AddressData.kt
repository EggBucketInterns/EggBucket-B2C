package com.eggbucket.eggbucket_b2c

data class UserA(
    val userId: String,        // Unique user ID
    val name: String,          // User's full name
    val email: String,         // User's email address
    val phone: String,         // User's phone number
    val addresses: List<UserAddressA> // List of user addresses
)
data class UserAddressA(
    val id: String,            // Unique ID of the address
    val fullAddress: FullAddress // Nested object with full address details
)