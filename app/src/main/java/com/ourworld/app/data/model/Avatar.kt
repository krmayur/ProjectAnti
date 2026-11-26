package com.ourworld.app.data.model

data class Avatar(
    val ownerId: String = "",
    val skinTone: Int = 0,
    val hairStyle: Int = 0,
    val hairColor: Int = 0,
    val eyeStyle: Int = 0,
    val outfitTop: Int = 0,
    val outfitBottom: Int = 0,
    val accessory: Int = 0,
    val hat: Int = 0,
    val shoes: Int = 0,
    val expression: String = "happy",
    val currentAnimation: String = "idle",
    val positionX: Float = 0.5f,
    val positionY: Float = 0.7f
)

// Enums for avatar options
enum class SkinTone(val label: String, val color: Long) {
    LIGHT("Light", 0xFFFDE8D0),
    MEDIUM_LIGHT("Medium Light", 0xFFE8C4A0),
    MEDIUM("Medium", 0xFFD4A574),
    MEDIUM_DARK("Medium Dark", 0xFFB07A4A),
    DARK("Dark", 0xFF8B5E3C),
    DEEP("Deep", 0xFF5C3A1E)
}

enum class HairStyle(val label: String) {
    SHORT("Short"),
    MEDIUM("Medium"),
    LONG("Long"),
    CURLY("Curly"),
    PONYTAIL("Ponytail"),
    BUN("Bun"),
    BRAIDS("Braids"),
    PIXIE("Pixie")
}

enum class HairColor(val label: String, val color: Long) {
    BLACK("Black", 0xFF1A1A1A),
    BROWN("Brown", 0xFF6B3A2A),
    BLONDE("Blonde", 0xFFE8C872),
    RED("Red", 0xFFC04030),
    PINK("Pink", 0xFFE57A90),
    PURPLE("Purple", 0xFF9B59B6),
    BLUE("Blue", 0xFF5B8DEF),
    SILVER("Silver", 0xFFC0C0C0)
}

enum class AvatarAnimation(val label: String, val emoji: String) {
    IDLE("Standing", "🧍"),
    WAVE("Waving", "👋"),
    HUG("Hugging", "🤗"),
    DANCE("Dancing", "💃"),
    SIT("Sitting", "🪑"),
    HOLD_HANDS("Holding Hands", "🤝"),
    BLOW_KISS("Blow Kiss", "😘"),
    HEART("Sending Love", "❤️")
}
