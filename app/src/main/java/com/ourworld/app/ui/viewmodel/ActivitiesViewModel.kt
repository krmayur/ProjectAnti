package com.ourworld.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ourworld.app.data.model.ActivityType
import com.ourworld.app.data.model.CoupleChallenge
import com.ourworld.app.data.model.DailyActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ActivitiesUiState(
    val dailyActivities: List<DailyActivity> = emptyList(),
    val challenges: List<CoupleChallenge> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class ActivitiesViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ActivitiesUiState())
    val uiState: StateFlow<ActivitiesUiState> = _uiState.asStateFlow()

    init {
        loadActivities()
    }

    private fun loadActivities() {
        val activities = listOf(
            DailyActivity(id = "1", title = "What made you smile today?", description = "Share a happy moment with your partner", type = ActivityType.QUESTION, xpReward = 10, coinReward = 5, emoji = "😊"),
            DailyActivity(id = "2", title = "Send a good morning message", description = "Start the day with love", type = ActivityType.CHALLENGE, xpReward = 10, coinReward = 5, emoji = "🌅"),
            DailyActivity(id = "3", title = "Decorate your home together", description = "Add a new furniture piece to your shared home", type = ActivityType.CREATIVE, xpReward = 15, coinReward = 10, emoji = "🏠"),
            DailyActivity(id = "4", title = "Plant something new", description = "Plant a seed in your garden together", type = ActivityType.CHALLENGE, xpReward = 15, coinReward = 10, emoji = "🌱"),
            DailyActivity(id = "5", title = "Share a favorite memory", description = "Tell your partner about a cherished memory", type = ActivityType.MEMORY, xpReward = 20, coinReward = 10, emoji = "📸")
        )
        val challenges = listOf(
            CoupleChallenge(id = "c1", title = "Green Thumbs", description = "Grow 3 plants together", difficulty = 1, xpReward = 50, coinReward = 25, emoji = "🌿"),
            CoupleChallenge(id = "c2", title = "Home Sweet Home", description = "Decorate with 10 furniture items", difficulty = 2, xpReward = 75, coinReward = 40, emoji = "🏡"),
            CoupleChallenge(id = "c3", title = "Gift Exchange", description = "Exchange 5 gifts", difficulty = 2, xpReward = 60, coinReward = 30, emoji = "🎁"),
            CoupleChallenge(id = "c4", title = "Memory Lane", description = "Create 10 memories together", difficulty = 3, xpReward = 100, coinReward = 50, emoji = "💕"),
            CoupleChallenge(id = "c5", title = "7-Day Streak", description = "Visit your home for 7 days in a row", difficulty = 2, xpReward = 75, coinReward = 35, emoji = "🔥")
        )
        _uiState.value = _uiState.value.copy(dailyActivities = activities, challenges = challenges)
    }

    fun completeActivity(activityId: String) {
        viewModelScope.launch {
            val updated = _uiState.value.dailyActivities.map { activity ->
                if (activity.id == activityId) activity.copy(isCompleted = true) else activity
            }
            _uiState.value = _uiState.value.copy(dailyActivities = updated)
        }
    }
}
