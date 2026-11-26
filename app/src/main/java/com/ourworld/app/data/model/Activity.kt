package com.ourworld.app.data.model

data class DailyActivity(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val type: ActivityType = ActivityType.QUESTION,
    val xpReward: Int = 10,
    val coinReward: Int = 5,
    val isCompleted: Boolean = false,
    val completedBy: List<String> = emptyList(),
    val date: String = "",
    val emoji: String = "✨"
)

enum class ActivityType(val label: String) {
    QUESTION("Question of the Day"),
    CHALLENGE("Couple Challenge"),
    GAME("Mini Game"),
    MEMORY("Memory Sharing"),
    CREATIVE("Creative Task")
}

data class CoupleChallenge(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val difficulty: Int = 1,
    val xpReward: Int = 25,
    val coinReward: Int = 15,
    val unlockableReward: String = "",
    val isCompleted: Boolean = false,
    val progress: Float = 0f,
    val emoji: String = "🏆"
)
