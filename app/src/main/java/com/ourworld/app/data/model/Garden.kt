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
    val growthBoostMillis: Long = 0L,
    val plantedBy: String = "",
    val positionX: Float = 0.5f,
    val positionY: Float = 0.5f,
    val season: Season = Season.SPRING
) {
    fun fullGrowthMillis(): Long = type.growHours * 60L * 60L * 1000L

    fun computedProgress(nowMs: Long = System.currentTimeMillis()): Float {
        val elapsed = (nowMs - plantedAt).coerceAtLeast(0L) + growthBoostMillis
        val total = fullGrowthMillis().coerceAtLeast(1L)
        return (elapsed.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    }

    fun computedStage(nowMs: Long = System.currentTimeMillis()): GrowthStage {
        val p = computedProgress(nowMs)
        return when {
            p >= 1f -> GrowthStage.FULLY_GROWN
            p >= 0.70f -> GrowthStage.BLOOMING
            p >= 0.40f -> GrowthStage.GROWING
            p >= 0.15f -> GrowthStage.SPROUT
            else -> GrowthStage.SEED
        }
    }

    fun timeRemainingLabel(nowMs: Long = System.currentTimeMillis()): String {
        val elapsed = (nowMs - plantedAt).coerceAtLeast(0L) + growthBoostMillis
        val remaining = (fullGrowthMillis() - elapsed).coerceAtLeast(0L)
        if (remaining == 0L) return "Ready to harvest! 🌾"
        val hours = remaining / (1000L * 60L * 60L)
        val mins = (remaining / (1000L * 60L)) % 60L
        return if (hours > 0) "${hours}h ${mins}m to bloom" else "${mins}m to bloom"
    }
}

enum class PlantType(
    val label: String,
    val emoji: String,
    val growHours: Int,
    val harvestCoins: Int,
    val harvestXp: Int
) {
    DAISY("Daisy", "🌼", 6, 15, 10),
    TULIP("Tulip", "🌷", 8, 20, 15),
    FERN("Fern", "🌿", 10, 22, 15),
    ROSE("Rose", "🌹", 12, 28, 20),
    LAVENDER("Lavender", "💜", 16, 32, 25),
    SUNFLOWER("Sunflower", "🌻", 20, 38, 30),
    CHERRY_BLOSSOM("Cherry Blossom", "🌸", 24, 45, 35),
    CACTUS("Cactus", "🌵", 30, 52, 40),
    BONSAI("Bonsai", "🌳", 36, 65, 50),
    LOVE_TREE("Love Tree", "💕", 48, 85, 65)
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
