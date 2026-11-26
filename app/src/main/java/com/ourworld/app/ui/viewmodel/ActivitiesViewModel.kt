package com.ourworld.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.ourworld.app.data.model.CoupleChallenge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ActivitiesUiState(
    val challenges: List<CoupleChallenge> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class ActivitiesViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ActivitiesUiState())
    val uiState: StateFlow<ActivitiesUiState> = _uiState.asStateFlow()

    init {
        loadChallenges()
    }

    private fun loadChallenges() {
        val challenges = listOf(
            CoupleChallenge(id = "c1", title = "Green Thumbs", description = "Grow 3 plants together", difficulty = 1, xpReward = 50, coinReward = 25, emoji = "🌿"),
            CoupleChallenge(id = "c2", title = "Home Sweet Home", description = "Decorate with 10 furniture items", difficulty = 2, xpReward = 75, coinReward = 40, emoji = "🏡"),
            CoupleChallenge(id = "c3", title = "Gift Exchange", description = "Exchange 5 gifts", difficulty = 2, xpReward = 60, coinReward = 30, emoji = "🎁"),
            CoupleChallenge(id = "c4", title = "Memory Lane", description = "Create 10 memories together", difficulty = 3, xpReward = 100, coinReward = 50, emoji = "💕"),
            CoupleChallenge(id = "c5", title = "7-Day Streak", description = "Visit your home for 7 days in a row", difficulty = 2, xpReward = 75, coinReward = 35, emoji = "🔥")
        )
        _uiState.value = _uiState.value.copy(challenges = challenges)
    }
}
