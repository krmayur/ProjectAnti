package com.ourworld.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ourworld.app.data.model.Gift
import com.ourworld.app.data.model.HandwrittenNote
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GiftRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun observeGifts(coupleId: String): Flow<List<Gift>> = callbackFlow {
        val listener = firestore.collection("couples").document(coupleId)
            .collection("gifts")
            .orderBy("sentAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val gifts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Gift::class.java)?.let { g ->
                        val openedFlag = (doc.getBoolean("isOpened") == true) ||
                            (doc.getBoolean("opened") == true) ||
                            g.isOpened
                        g.copy(isOpened = openedFlag)
                    }
                } ?: emptyList()
                trySend(gifts)
            }
        awaitClose { listener.remove() }
    }

    suspend fun sendGift(coupleId: String, gift: Gift): Result<Gift> {
        return try {
            val giftWithId = gift.copy(id = UUID.randomUUID().toString())
            firestore.collection("couples").document(coupleId)
                .collection("gifts").document(giftWithId.id).set(giftWithId).await()
            Result.success(giftWithId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun openGift(coupleId: String, giftId: String) {
        firestore.collection("couples").document(coupleId)
            .collection("gifts").document(giftId)
            .update(
                mapOf(
                    "isOpened" to true,
                    "opened" to true,
                    "openedAt" to System.currentTimeMillis()
                )
            ).await()
    }

    fun observeNotes(coupleId: String): Flow<List<HandwrittenNote>> = callbackFlow {
        val listener = firestore.collection("couples").document(coupleId)
            .collection("notes")
            .orderBy("sentAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val notes = snapshot?.toObjects(HandwrittenNote::class.java) ?: emptyList()
                trySend(notes)
            }
        awaitClose { listener.remove() }
    }

    suspend fun sendNote(coupleId: String, note: HandwrittenNote): Result<HandwrittenNote> {
        return try {
            val noteWithId = note.copy(id = UUID.randomUUID().toString())
            firestore.collection("couples").document(coupleId)
                .collection("notes").document(noteWithId.id).set(noteWithId).await()
            Result.success(noteWithId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
