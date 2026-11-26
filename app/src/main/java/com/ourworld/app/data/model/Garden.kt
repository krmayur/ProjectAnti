package com.ourworld.app.data.model

data class Plant(
    val id: String = "",
    val type: PlantType = PlantType.ROSE,
    val name: String = "",
    val stage: GrowthStage = GrowthStage.SEED,
    val health: Int = 100,
    val waterLevel: Int = 100,
    val lastWatered: Long = System.currentTimeMillis(),
    val plantedAt: Long = System.currentTimeMillis(),
    val plantedBy: String = "",
    val positionX: Float = 0.5f,
    val positionY: Float = 0.5f,
    val season: Season = Season.SPRING
)

enum class PlantType(val label: String, val emoji: String, val growDays: Int) {
    ROSE("Rose", "🌹", 3),
    SUNFLOWER("Sunflower", "🌻", 4),
    TULIP("Tulip", "🌷", 2),
    DAISY("Daisy", "🌼", 2),
    CHERRY_BLOSSOM("Cherry Blossom", "🌸", 5),
    CACTUS("Cactus", "🌵", 7),
    LAVENDER("Lavender", "💜", 4),
    FERN("Fern", "🌿", 3),
    BONSAI("Bonsai", "🌳", 10),
    LOVE_TREE("Love Tree", "💕", 14)
}

enum class GrowthStage(val label: String, val emoji: String) {
    SEED("Seed", "🫘"),
    SPROUT("Sprout", "🌱"),
    GROWING("Growing", "🪴"),
    BLOOMING("Blooming", "🌺"),
    FULLY_GROWN("Fully Grown", "🌳")
}

enum class Season(val label: String, val emoji: String) {
    SPRING("Spring", "🌸"),
    SUMMER("Summer", "☀️"),
    AUTUMN("Autumn", "🍂"),
    WINTER("Winter", "❄️")
}
