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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class ActivitiesUiState(
    val todayLabel: String = "",
    val dailyChallenges: List<CoupleChallenge> = emptyList(),
    val milestoneChallenges: List<CoupleChallenge> = emptyList(),
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
    private var noteCount = 0
    private var memoryCount = 0
    private var claimedIds = emptySet<String>()
    private var currentUserId = ""

    private val todayKey: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    private val todayDisplay: String
        get() = SimpleDateFormat("MMM d", Locale.US).format(Date())

    init {
        rebuildChallenges()
    }

    private fun startOfTodayMillis(): Long {
        return LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    fun loadChallenges(coupleId: String, userId: String) {
        if (coupleId.isEmpty()) return
        currentUserId = userId
        val coupleRef = firestore.collection("couples").document(coupleId)

        coupleRef.collection("plants").addSnapshotListener { snap, _ ->
            val todayStart = startOfTodayMillis()
            plantCount = snap?.documents?.count { (it.getLong("plantedAt") ?: 0L) >= todayStart } ?: 0
            rebuildChallenges()
        }
        coupleRef.collection("furniture").addSnapshotListener { snap, _ ->
            val todayStart = startOfTodayMillis()
            furnitureCount = snap?.documents?.count { (it.getLong("addedAt") ?: 0L) >= todayStart } ?: 0
            rebuildChallenges()
        }
        coupleRef.collection("gifts").addSnapshotListener { snap, _ ->
            val todayStart = startOfTodayMillis()
            giftCount = snap?.documents?.count { (it.getLong("sentAt") ?: 0L) >= todayStart } ?: 0
            rebuildChallenges()
        }
        coupleRef.collection("notes").addSnapshotListener { snap, _ ->
            val todayStart = startOfTodayMillis()
            noteCount = snap?.documents?.count { (it.getLong("sentAt") ?: 0L) >= todayStart } ?: 0
            rebuildChallenges()
        }
        coupleRef.collection("memories").addSnapshotListener { snap, _ ->
            val todayStart = startOfTodayMillis()
            memoryCount = snap?.documents?.count { (it.getLong("date") ?: 0L) >= todayStart } ?: 0
            rebuildChallenges()
        }
        coupleRef.collection("claimedChallenges").addSnapshotListener { snap, _ ->
            claimedIds = snap?.documents?.map { it.id }?.toSet() ?: emptySet()
            rebuildChallenges()
        }
    }

    private fun rebuildChallenges() {
        val dateKey = todayKey
        val dayIndex = (System.currentTimeMillis() / (1000L * 60L * 60L * 24L)).toInt()

        // Pool of rotating daily couple challenges — 3 fresh ones picked each day
        val dailyPool = listOf(
            CoupleChallenge(
                id = "daily_${dateKey}_checkin_$currentUserId",
                title = "Cozy Check-In",
                description = "Visit your shared home together today",
                xpReward = 25,
                coinReward = 20,
                progress = 1f,
                emoji = "🏠"
            ),
            CoupleChallenge(
                id = "daily_${dateKey}_garden_$currentUserId",
                title = "Bloom Together",
                description = "Have at least 1 plant growing in your garden ($plantCount/1)",
                xpReward = 35,
                coinReward = 25,
                progress = (plantCount / 1f).coerceIn(0f, 1f),
                emoji = "🌷"
            ),
            CoupleChallenge(
                id = "daily_${dateKey}_sweet_$currentUserId",
                title = "Sweet Thought",
                description = "Send a gift or love note (${giftCount + noteCount}/1)",
                xpReward = 40,
                coinReward = 30,
                progress = ((giftCount + noteCount) / 1f).coerceIn(0f, 1f),
                emoji = "💌"
            ),
            CoupleChallenge(
                id = "daily_${dateKey}_decor_$currentUserId",
                title = "Interior Touch",
                description = "Style your home with 5+ items ($furnitureCount/5)",
                xpReward = 30,
                coinReward = 25,
                progress = (furnitureCount / 5f).coerceIn(0f, 1f),
                emoji = "🛋️"
            ),
            CoupleChallenge(
                id = "daily_${dateKey}_memory_$currentUserId",
                title = "Capture the Moment",
                description = "Save a shared memory ($memoryCount/1)",
                xpReward = 45,
                coinReward = 35,
                progress = (memoryCount / 1f).coerceIn(0f, 1f),
                emoji = "📸"
            ),
            CoupleChallenge(
                id = "daily_${dateKey}_love_$currentUserId",
                title = "Daily Love Spark",
                description = "Share a warm moment in OurWorld today",
                xpReward = 25,
                coinReward = 20,
                progress = 1f,
                emoji = "✨"
            )
        )

        // Deterministically pick 3 challenges for today so they rotate every 24h
        val start = dayIndex % dailyPool.size
        val todaysPicks = (0 until 3).map { offset ->
            val item = dailyPool[(start + offset) % dailyPool.size]
            item.copy(isCompleted = item.id in claimedIds)
        }

        val milestones = listOf(
            CoupleChallenge(
                id = "milestone_${dateKey}_c1_$currentUserId",
                title = "Green Thumbs",
                description = "Plant 3 seeds in your garden today ($plantCount/3)",
                difficulty = 1,
                xpReward = 50,
                coinReward = 25,
                progress = (plantCount / 3f).coerceIn(0f, 1f),
                isCompleted = "milestone_${dateKey}_c1_$currentUserId" in claimedIds,
                emoji = "🌿"
            ),
            CoupleChallenge(
                id = "milestone_${dateKey}_c2_$currentUserId",
                title = "Home Sweet Home",
                description = "Add 3 furniture items today ($furnitureCount/3)",
                difficulty = 2,
                xpReward = 75,
                coinReward = 40,
                progress = (furnitureCount / 3f).coerceIn(0f, 1f),
                isCompleted = "milestone_${dateKey}_c2_$currentUserId" in claimedIds,
                emoji = "🏡"
            ),
            CoupleChallenge(
                id = "milestone_${dateKey}_c3_$currentUserId",
                title = "Gift Exchange",
                description = "Send 3 gifts today ($giftCount/3)",
                difficulty = 2,
                xpReward = 60,
                coinReward = 30,
                progress = (giftCount / 3f).coerceIn(0f, 1f),
                isCompleted = "milestone_${dateKey}_c3_$currentUserId" in claimedIds,
                emoji = "🎁"
            ),
            CoupleChallenge(
                id = "milestone_${dateKey}_c4_$currentUserId",
                title = "Memory Lane",
                description = "Create 3 memories today ($memoryCount/3)",
                difficulty = 3,
                xpReward = 100,
                coinReward = 50,
                progress = (memoryCount / 3f).coerceIn(0f, 1f),
                isCompleted = "milestone_${dateKey}_c4_$currentUserId" in claimedIds,
                emoji = "💕"
            )
        )

        _uiState.value = _uiState.value.copy(
            todayLabel = todayDisplay,
            dailyChallenges = todaysPicks,
            milestoneChallenges = milestones
        )
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
