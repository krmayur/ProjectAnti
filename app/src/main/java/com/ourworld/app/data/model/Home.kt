package com.ourworld.app.data.model

data class Home(
    val coupleId: String = "",
    val wallpaper: Int = 0,
    val flooring: Int = 0,
    val lighting: Int = 0,
    val ambiance: String = "cozy",
    val roomLevel: Int = 1,
    val lastUpdated: Long = System.currentTimeMillis()
)

data class FurnitureItem(
    val id: String = "",
    val type: String = "",
    val name: String = "",
    val category: FurnitureCategory = FurnitureCategory.SEATING,
    val positionX: Float = 0.5f,
    val positionY: Float = 0.5f,
    val rotation: Float = 0f,
    val scale: Float = 1f,
    val isUnlocked: Boolean = true,
    val requiredLevel: Int = 1,
    val cost: Int = 0,
    val emoji: String = "🪑",
    val placedBy: String = ""
)

enum class FurnitureCategory(val label: String, val emoji: String) {
    SEATING("Seating", "🛋️"),
    TABLES("Tables", "🪑"),
    LIGHTING("Lighting", "💡"),
    DECORATION("Decoration", "🖼️"),
    PLANTS("Plants", "🌿"),
    BEDROOM("Bedroom", "🛏️"),
    KITCHEN("Kitchen", "🍳"),
    ENTERTAINMENT("Entertainment", "📺"),
    ROMANTIC("Romantic", "🕯️"),
    SEASONAL("Seasonal", "🎄")
}

enum class Wallpaper(val label: String, val color: Long) {
    CREAM("Cream", 0xFFFFF8F2),
    BLUSH("Blush Pink", 0xFFFFE7DC),
    LAVENDER("Lavender", 0xFFE8E0F0),
    MINT("Mint", 0xFFE0F5EC),
    SKY("Sky Blue", 0xFFE0EEFF),
    SUNSET("Sunset", 0xFFFFE0CC),
    COZY_BROWN("Cozy Brown", 0xFFF5E6D3),
    STARRY("Starry Night", 0xFF2C3E6B)
}

enum class Lighting(val label: String, val emoji: String) {
    WARM("Warm & Cozy", "🕯️"),
    BRIGHT("Bright & Clear", "☀️"),
    ROMANTIC("Romantic Dim", "💜"),
    FAIRY("Fairy Lights", "✨"),
    SUNSET("Sunset Glow", "🌅"),
    MOONLIGHT("Moonlight", "🌙")
}
