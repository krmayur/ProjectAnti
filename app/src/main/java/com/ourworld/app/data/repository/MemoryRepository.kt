package com.ourworld.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.ourworld.app.data.model.Achievement
import com.ourworld.app.data.model.Memory
import com.ourworld.app.data.model.Milestone
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    fun observeMemories(coupleId: String): Flow<List<Memory>> = callbackFlow {
        val listener = firestore.collection("couples").document(coupleId)
            .collection("memories")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val memories = snapshot?.toObjects(Memory::class.java) ?: emptyList()
                trySend(memories)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addMemory(coupleId: String, memory: Memory): Result<Memory> {
        return try {
            val memoryWithId = memory.copy(id = UUID.randomUUID().toString(), coupleId = coupleId)
            firestore.collection("couples").document(coupleId)
                .collection("memories").document(memoryWithId.id).set(memoryWithId).await()
            Result.success(memoryWithId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeAchievements(coupleId: String): Flow<List<Achievement>> = callbackFlow {
        val listener = firestore.collection("couples").document(coupleId)
            .collection("achievements")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val achievements = snapshot?.toObjects(Achievement::class.java) ?: emptyList()
                trySend(achievements)
            }
        awaitClose { listener.remove() }
    }

    fun observeMilestones(coupleId: String): Flow<List<Milestone>> = callbackFlow {
        val listener = firestore.collection("couples").document(coupleId)
            .collection("milestones")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val milestones = snapshot?.toObjects(Milestone::class.java) ?: emptyList()
                trySend(milestones)
            }
        awaitClose { listener.remove() }
    }

    suspend fun initializeDefaultAchievements(coupleId: String) {
        val defaults = listOf(
            Achievement(id = "first_login", title = "Welcome Home", description = "Log in for the first time", emoji = "🏠", requirement = 1),
            Achievement(id = "first_gift", title = "Gift Giver", description = "Send your first gift", emoji = "🎁", requirement = 1),
            Achievement(id = "green_thumb", title = "Green Thumb", description = "Grow 5 plants to full bloom", emoji = "🌱", requirement = 5),
            Achievement(id = "decorator", title = "Interior Designer", description = "Place 10 furniture items", emoji = "🛋️", requirement = 10),
            Achievement(id = "love_letters", title = "Love Letters", description = "Send 10 notes", emoji = "💌", requirement = 10),
            Achievement(id = "week_streak", title = "7-Day Streak", description = "Visit for 7 consecutive days", emoji = "🔥", requirement = 7),
            Achievement(id = "month_together", title = "One Month Together", description = "Be paired for 30 days", emoji = "💕", requirement = 30),
            Achievement(id = "garden_master", title = "Garden Master", description = "Grow every type of plant", emoji = "🌳", requirement = 10),
            Achievement(id = "social_butterfly", title = "Social Butterfly", description = "Complete 20 activities", emoji = "🦋", requirement = 20),
            Achievement(id = "memory_keeper", title = "Memory Keeper", description = "Save 25 memories", emoji = "📸", requirement = 25)
        )
        val batch = firestore.batch()
        defaults.forEach { achievement ->
            batch.set(
                firestore.collection("couples").document(coupleId)
                    .collection("achievements").document(achievement.id),
                achievement
            )
        }
        batch.commit().await()
    }

    suspend fun initializeDefaultMilestones(coupleId: String) {
        val defaults = listOf(
            Milestone(id = "day_1", coupleId = coupleId, title = "First Day Together", description = "You started your journey!", daysTogetherRequired = 1, emoji = "🌟"),
            Milestone(id = "week_1", coupleId = coupleId, title = "One Week Together", description = "A beautiful week!", daysTogetherRequired = 7, emoji = "💫"),
            Milestone(id = "month_1", coupleId = coupleId, title = "One Month Together", description = "A whole month of love!", daysTogetherRequired = 30, emoji = "🌙"),
            Milestone(id = "month_3", coupleId = coupleId, title = "Three Months", description = "Growing stronger together!", daysTogetherRequired = 90, emoji = "💝"),
            Milestone(id = "month_6", coupleId = coupleId, title = "Half Year of Love", description = "Six months of happiness!", daysTogetherRequired = 180, emoji = "🎉"),
            Milestone(id = "year_1", coupleId = coupleId, title = "One Year Anniversary", description = "A whole year together!", daysTogetherRequired = 365, emoji = "🏆"),
            Milestone(id = "year_2", coupleId = coupleId, title = "Two Years Together", description = "Love only grows!", daysTogetherRequired = 730, emoji = "💖")
        )
        val batch = firestore.batch()
        defaults.forEach { milestone ->
            batch.set(
                firestore.collection("couples").document(coupleId)
                    .collection("milestones").document(milestone.id),
                milestone
            )
        }
        batch.commit().await()
    }
}
