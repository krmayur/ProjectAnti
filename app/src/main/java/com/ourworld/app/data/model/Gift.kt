package com.ourworld.app.data.model

data class Gift(
    val id: String = "",
    val fromUserId: String = "",
    val toUserId: String = "",
    val type: GiftType = GiftType.FLOWER,
    val message: String = "",
    val hiddenMessage: String = "",
    val isOpened: Boolean = false,
    val isHidden: Boolean = false,
    val sentAt: Long = System.currentTimeMillis(),
    val openedAt: Long = 0,
    val emoji: String = "🎁"
)

enum class GiftType(val label: String, val emoji: String, val cost: Int) {
    FLOWER("Flower Bouquet", "💐", 10),
    CHOCOLATE("Chocolates", "🍫", 15),
    TEDDY("Teddy Bear", "🧸", 20),
    LETTER("Love Letter", "💌", 5),
    MUSIC_BOX("Music Box", "🎵", 25),
    STAR("Naming a Star", "⭐", 30),
    RING("Promise Ring", "💍", 50),
    PHOTO_FRAME("Photo Frame", "🖼️", 20),
    CANDLE("Scented Candle", "🕯️", 15),
    HEART("Heart Balloon", "🎈", 10)
}

data class HandwrittenNote(
    val id: String = "",
    val fromUserId: String = "",
    val content: String = "",
    val backgroundColor: Long = 0xFFFFF8F2,
    val textColor: Long = 0xFF5C3A1E,
    val sentAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
