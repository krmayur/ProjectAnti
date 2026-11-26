package com.ourworld.app.util

import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import java.time.LocalTime

enum class TimeOfDay(val label: String, val emoji: String) {
    DAWN("Dawn", "🌅"),
    DAY("Day", "☀️"),
    EVENING("Evening", "🌇"),
    NIGHT("Night", "🌙");

    val isNight: Boolean get() = this == NIGHT
}

fun currentTimeOfDay(now: LocalTime = LocalTime.now()): TimeOfDay {
    return when (now.hour) {
        in 5..7 -> TimeOfDay.DAWN
        in 8..16 -> TimeOfDay.DAY
        in 17..19 -> TimeOfDay.EVENING
        else -> TimeOfDay.NIGHT
    }
}

@Composable
fun rememberPhoneTimeOfDay(): TimeOfDay {
    var timeOfDay by remember { mutableStateOf(currentTimeOfDay()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000L)
            timeOfDay = currentTimeOfDay()
        }
    }
    return timeOfDay
}
