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
    companion object {
        const val FULL_GROWTH_MILLIS: Long = 24L * 60L * 60L * 1000L // 1 day
    }

    fun computedProgress(nowMs: Long = System.currentTimeMillis()): Float {
        val elapsed = (nowMs - plantedAt).coerceAtLeast(0L) + growthBoostMillis
        return (elapsed.toFloat() / FULL_GROWTH_MILLIS.toFloat()).coerceIn(0f, 1f)
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
        val remaining = (FULL_GROWTH_MILLIS - elapsed).coerceAtLeast(0L)
        if (remaining == 0L) return "Full Bloom! 🌸"
        val hours = remaining / (1000L * 60L * 60L)
        val mins = (remaining / (1000L * 60L)) % 60L
        return if (hours > 0) "${hours}h ${mins}m to bloom" else "${mins}m to bloom"
    }
}

enum class PlantType(val label: String, val emoji: String, val growDays: Int) {
    ROSE("Rose", "🌹", 1),
    SUNFLOWER("Sunflower", "🌻", 1),
    TULIP("Tulip", "🌷", 1),
    DAISY("Daisy", "🌼", 1),
    CHERRY_BLOSSOM("Cherry Blossom", "🌸", 1),
    CACTUS("Cactus", "🌵", 1),
    LAVENDER("Lavender", "💜", 1),
    FERN("Fern", "🌿", 1),
    BONSAI("Bonsai", "🌳", 1),
    LOVE_TREE("Love Tree", "💕", 1)
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
