package com.ourworld.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.ourworld.app.data.model.CoupleChallenge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class ActivitiesUiState(
    val challenges: List<CoupleChallenge> = emptyList(),
    val rewardMessage: String? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class ActivitiesViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivitiesUiState())
    val uiState: StateFlow<ActivitiesUiState> = _uiState.asStateFlow()

    private var plantCount = 0
    private var furnitureCount = 0
    private var giftCount = 0
    private var memoryCount = 0
    private var claimedIds = emptySet<String>()

    init {
        rebuildChallenges()
    }

    fun loadChallenges(coupleId: String) {
        if (coupleId.isEmpty()) return
        val coupleRef = firestore.collection("couples").document(coupleId)

        coupleRef.collection("plants").addSnapshotListener { snap, _ ->
            plantCount = snap?.size() ?: 0
            rebuildChallenges()
        }
        coupleRef.collection("furniture").addSnapshotListener { snap, _ ->
            furnitureCount = snap?.size() ?: 0
            rebuildChallenges()
        }
        coupleRef.collection("gifts").addSnapshotListener { snap, _ ->
            giftCount = snap?.size() ?: 0
            rebuildChallenges()
        }
        coupleRef.collection("memories").addSnapshotListener { snap, _ ->
            memoryCount = snap?.size() ?: 0
            rebuildChallenges()
        }
        coupleRef.collection("claimedChallenges").addSnapshotListener { snap, _ ->
            claimedIds = snap?.documents?.map { it.id }?.toSet() ?: emptySet()
            rebuildChallenges()
        }
    }

    private fun rebuildChallenges() {
        val list = listOf(
            CoupleChallenge(
                id = "c0",
                title = "Welcome Home",
                description = "Move into your shared home together",
                difficulty = 1,
                xpReward = 30,
                coinReward = 25,
                progress = 1f,
                isCompleted = "c0" in claimedIds,
                emoji = "🏠"
            ),
            CoupleChallenge(
                id = "c1",
                title = "Green Thumbs",
                description = "Plant 3 seeds in your garden ($plantCount/3)",
                difficulty = 1,
                xpReward = 50,
                coinReward = 25,
                progress = (plantCount / 3f).coerceIn(0f, 1f),
                isCompleted = "c1" in claimedIds,
                emoji = "🌿"
            ),
            CoupleChallenge(
                id = "c2",
                title = "Home Sweet Home",
                description = "Decorate with 6 furniture items ($furnitureCount/6)",
                difficulty = 2,
                xpReward = 75,
                coinReward = 40,
                progress = (furnitureCount / 6f).coerceIn(0f, 1f),
                isCompleted = "c2" in claimedIds,
                emoji = "🏡"
            ),
            CoupleChallenge(
                id = "c3",
                title = "Gift Exchange",
                description = "Send 3 gifts to each other ($giftCount/3)",
                difficulty = 2,
                xpReward = 60,
                coinReward = 30,
                progress = (giftCount / 3f).coerceIn(0f, 1f),
                isCompleted = "c3" in claimedIds,
                emoji = "🎁"
            ),
            CoupleChallenge(
                id = "c4",
                title = "Memory Lane",
                description = "Create 5 memories together ($memoryCount/5)",
                difficulty = 3,
                xpReward = 100,
                coinReward = 50,
                progress = (memoryCount / 5f).coerceIn(0f, 1f),
                isCompleted = "c4" in claimedIds,
                emoji = "💕"
            )
        )
        _uiState.value = _uiState.value.copy(challenges = list)
    }

    fun claimReward(coupleId: String, userId: String, challenge: CoupleChallenge) {
        if (coupleId.isEmpty() || userId.isEmpty() || challenge.isCompleted || challenge.progress < 1f) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val batch = firestore.batch()
                val claimRef = firestore.collection("couples").document(coupleId)
                    .collection("claimedChallenges").document(challenge.id)
                batch.set(
                    claimRef,
                    mapOf(
                        "challengeId" to challenge.id,
                        "claimedBy" to userId,
                        "claimedAt" to System.currentTimeMillis()
                    )
                )
                val userRef = firestore.collection("users").document(userId)
                batch.update(
                    userRef,
                    mapOf(
                        "coins" to FieldValue.increment(challenge.coinReward.toLong()),
                        "xp" to FieldValue.increment(challenge.xpReward.toLong())
                    )
                )
                batch.commit().await()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    rewardMessage = "🎉 Claimed ${challenge.title}! +${challenge.coinReward} 🪙 & +${challenge.xpReward} XP"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    rewardMessage = "Could not claim reward: ${e.message}"
                )
            }
        }
    }

    fun clearRewardMessage() {
        _uiState.value = _uiState.value.copy(rewardMessage = null)
    }
}
