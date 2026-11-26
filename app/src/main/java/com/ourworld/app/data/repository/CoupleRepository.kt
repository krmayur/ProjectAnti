package com.ourworld.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ourworld.app.data.model.Avatar
import com.ourworld.app.data.model.FurnitureItem
import com.ourworld.app.data.model.Home
import com.ourworld.app.data.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoupleRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend fun pairWithCode(code: String): Result<String> {
        return try {
            val currentUid = auth.currentUser?.uid ?: throw Exception("Not logged in")
            // Find user with matching pairing code
            val query = firestore.collection("users")
                .whereEqualTo("pairingCode", code)
                .get().await()
            if (query.isEmpty) throw Exception("Invalid pairing code")
            val partnerDoc = query.documents.first()
            val partnerId = partnerDoc.id
            if (partnerId == currentUid) throw Exception("Cannot pair with yourself")

            val coupleId = if (currentUid < partnerId) "${currentUid}_${partnerId}" else "${partnerId}_${currentUid}"

            // Create couple document
            val home = Home(coupleId = coupleId)
            firestore.collection("couples").document(coupleId).set(
                mapOf(
                    "coupleId" to coupleId,
                    "partner1" to currentUid,
                    "partner2" to partnerId,
                    "createdAt" to System.currentTimeMillis(),
                    "home" to home
                )
            ).await()

            // Update both users (write both "paired" and "isPaired" for Firestore Kotlin serialization)
            val batch = firestore.batch()
            batch.update(firestore.collection("users").document(currentUid),
                mapOf("coupleId" to coupleId, "isPaired" to true, "paired" to true, "partnerId" to partnerId))
            batch.update(firestore.collection("users").document(partnerId),
                mapOf("coupleId" to coupleId, "isPaired" to true, "paired" to true, "partnerId" to currentUid))
            batch.commit().await()

            // Add default furniture
            addDefaultFurniture(coupleId)

            Result.success(coupleId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unpair(currentUid: String, partnerId: String): Result<Unit> {
        return try {
            val resetData = mapOf(
                "coupleId" to "",
                "isPaired" to false,
                "paired" to false,
                "partnerId" to ""
            )
            val batch = firestore.batch()
            batch.update(firestore.collection("users").document(currentUid), resetData)
            if (partnerId.isNotEmpty()) {
                batch.update(firestore.collection("users").document(partnerId), resetData)
            }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeHome(coupleId: String): Flow<Home?> = callbackFlow {
        val listener = firestore.collection("couples").document(coupleId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val homeMap = snapshot?.get("home") as? Map<*, *>
                if (homeMap != null) {
                    trySend(Home(
                        coupleId = homeMap["coupleId"] as? String ?: "",
                        wallpaper = (homeMap["wallpaper"] as? Long)?.toInt() ?: 0,
                        flooring = (homeMap["flooring"] as? Long)?.toInt() ?: 0,
                        lighting = (homeMap["lighting"] as? Long)?.toInt() ?: 0,
                        ambiance = homeMap["ambiance"] as? String ?: "cozy",
                        roomLevel = (homeMap["roomLevel"] as? Long)?.toInt() ?: 1
                    ))
                } else {
                    trySend(null)
                }
            }
        awaitClose { listener.remove() }
    }

    fun observeFurniture(coupleId: String): Flow<List<FurnitureItem>> = callbackFlow {
        val listener = firestore.collection("couples").document(coupleId)
            .collection("furniture")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val items = snapshot?.toObjects(FurnitureItem::class.java) ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateHome(coupleId: String, home: Home) {
        firestore.collection("couples").document(coupleId)
            .update("home", home).await()
    }

    suspend fun addFurniture(coupleId: String, item: FurnitureItem) {
        firestore.collection("couples").document(coupleId)
            .collection("furniture").document(item.id).set(item).await()
    }

    suspend fun updateFurniturePosition(coupleId: String, itemId: String, x: Float, y: Float) {
        firestore.collection("couples").document(coupleId)
            .collection("furniture").document(itemId)
            .update(mapOf("positionX" to x, "positionY" to y)).await()
    }

    suspend fun removeFurniture(coupleId: String, itemId: String) {
        firestore.collection("couples").document(coupleId)
            .collection("furniture").document(itemId).delete().await()
    }

    fun observePartnerAvatar(partnerId: String): Flow<Avatar?> = callbackFlow {
        val listener = firestore.collection("users").document(partnerId)
            .collection("avatar").document("current")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(Avatar::class.java))
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateAvatar(userId: String, avatar: Avatar) {
        firestore.collection("users").document(userId)
            .collection("avatar").document("current").set(avatar).await()
    }

    suspend fun updateAvatarAnimation(userId: String, animation: String) {
        firestore.collection("users").document(userId)
            .collection("avatar").document("current")
            .update("currentAnimation", animation).await()
    }

    private suspend fun addDefaultFurniture(coupleId: String) {
        val defaults = listOf(
            FurnitureItem(id = "sofa_1", type = "sofa", name = "Cozy Sofa", emoji = "🛋️", positionX = 0.3f, positionY = 0.6f),
            FurnitureItem(id = "table_1", type = "table", name = "Coffee Table", emoji = "🪑", positionX = 0.5f, positionY = 0.65f),
            FurnitureItem(id = "lamp_1", type = "lamp", name = "Warm Lamp", emoji = "💡", positionX = 0.15f, positionY = 0.4f),
            FurnitureItem(id = "plant_1", type = "plant", name = "Little Plant", emoji = "🌿", positionX = 0.85f, positionY = 0.45f),
            FurnitureItem(id = "frame_1", type = "frame", name = "Photo Frame", emoji = "🖼️", positionX = 0.5f, positionY = 0.25f)
        )
        val batch = firestore.batch()
        defaults.forEach { item ->
            batch.set(
                firestore.collection("couples").document(coupleId)
                    .collection("furniture").document(item.id),
                item
            )
        }
        batch.commit().await()
    }
}
