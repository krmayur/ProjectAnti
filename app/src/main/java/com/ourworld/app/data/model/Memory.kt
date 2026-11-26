package com.ourworld.app.data.model

data class Memory(
    val id: String = "",
    val coupleId: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val date: Long = System.currentTimeMillis(),
    val type: MemoryType = MemoryType.MOMENT,
    val addedBy: String = "",
    val emoji: String = "📸"
)

enum class MemoryType(val label: String, val emoji: String) {
    MOMENT("Special Moment", "📸"),
    MILESTONE("Milestone", "🏆"),
    ANNIVERSARY("Anniversary", "💝"),
    FIRST("First Time", "⭐"),
    ACHIEVEMENT("Achievement", "🎖️"),
    GARDEN("Garden Memory", "🌱")
}

data class Achievement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val emoji: String = "🏆",
    val isUnlocked: Boolean = false,
    val unlockedAt: Long = 0,
    val progress: Float = 0f,
    val requirement: Int = 1,
    val current: Int = 0
)

data class Milestone(
    val id: String = "",
    val coupleId: String = "",
    val title: String = "",
    val description: String = "",
    val date: Long = System.currentTimeMillis(),
    val daysTogetherRequired: Int = 0,
    val isReached: Boolean = false,
    val emoji: String = "💕"
)
