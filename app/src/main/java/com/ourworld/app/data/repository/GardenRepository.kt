package com.ourworld.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.ourworld.app.data.model.GrowthStage
import com.ourworld.app.data.model.Plant
import com.ourworld.app.data.model.PlantType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GardenRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun observePlants(coupleId: String): Flow<List<Plant>> = callbackFlow {
        val listener = firestore.collection("couples").document(coupleId)
            .collection("plants")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val plants = snapshot?.toObjects(Plant::class.java) ?: emptyList()
                trySend(plants)
            }
        awaitClose { listener.remove() }
    }

    suspend fun plantSeed(coupleId: String, plantType: PlantType, userId: String, x: Float, y: Float): Result<Plant> {
        return try {
            val plant = Plant(
                id = UUID.randomUUID().toString(),
                type = plantType,
                name = plantType.label,
                stage = GrowthStage.SEED,
                plantedBy = userId,
                positionX = x,
                positionY = y
            )
            firestore.collection("couples").document(coupleId)
                .collection("plants").document(plant.id).set(plant).await()
            Result.success(plant)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun waterPlant(coupleId: String, plantId: String) {
        val plantRef = firestore.collection("couples").document(coupleId)
            .collection("plants").document(plantId)
        val doc = plantRef.get().await()
        val plant = doc.toObject(Plant::class.java) ?: return
        // Each watering adds 90 minutes of growth boost toward the 24h bloom cycle
        val newBoost = (plant.growthBoostMillis + 90L * 60L * 1000L)
            .coerceAtMost(Plant.FULL_GROWTH_MILLIS)
        val updatedPlant = plant.copy(
            waterLevel = minOf(100, plant.waterLevel + 30),
            lastWatered = System.currentTimeMillis(),
            health = minOf(100, plant.health + 10),
            growthBoostMillis = newBoost
        )
        val newStage = updatedPlant.computedStage()
        plantRef.set(updatedPlant.copy(stage = newStage)).await()
    }

    suspend fun updatePlantPosition(coupleId: String, plantId: String, x: Float, y: Float) {
        firestore.collection("couples").document(coupleId)
            .collection("plants").document(plantId)
            .update(mapOf("positionX" to x, "positionY" to y)).await()
    }

    suspend fun removePlant(coupleId: String, plantId: String) {
        firestore.collection("couples").document(coupleId)
            .collection("plants").document(plantId).delete().await()
    }
}
