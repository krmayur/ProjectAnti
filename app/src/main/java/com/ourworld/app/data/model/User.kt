package com.ourworld.app.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val coupleId: String = "",
    val pairingCode: String = "",
    val isPaired: Boolean = false,
    val partnerId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val lastOnline: Long = System.currentTimeMillis(),
    val coins: Int = 100,
    val level: Int = 1,
    val xp: Int = 0
)
