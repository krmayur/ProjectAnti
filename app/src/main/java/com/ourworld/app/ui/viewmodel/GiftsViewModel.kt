package com.ourworld.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ourworld.app.data.model.Gift
import com.ourworld.app.data.model.GiftType
import com.ourworld.app.data.model.HandwrittenNote
import com.ourworld.app.data.repository.GiftRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class GiftsUiState(
    val gifts: List<Gift> = emptyList(),
    val notes: List<HandwrittenNote> = emptyList(),
    val isSendingGift: Boolean = false,
    val isWritingNote: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class GiftsViewModel @Inject constructor(
    private val giftRepository: GiftRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GiftsUiState())
    val uiState: StateFlow<GiftsUiState> = _uiState.asStateFlow()

    private fun startOfTodayMillis(): Long {
        return LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    fun loadGifts(coupleId: String) {
        viewModelScope.launch {
            giftRepository.observeGifts(coupleId).collect { gifts ->
                val todayStart = startOfTodayMillis()
                _uiState.value = _uiState.value.copy(
                    gifts = gifts.filter { it.sentAt >= todayStart }
                )
            }
        }
        viewModelScope.launch {
            giftRepository.observeNotes(coupleId).collect { notes ->
                val todayStart = startOfTodayMillis()
                _uiState.value = _uiState.value.copy(
                    notes = notes.filter { it.sentAt >= todayStart }
                )
            }
        }
    }

    fun sendGift(coupleId: String, giftType: GiftType, fromUserId: String, toUserId: String, message: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val gift = Gift(
                type = giftType,
                fromUserId = fromUserId,
                toUserId = toUserId,
                message = message,
                emoji = giftType.emoji
            )
            giftRepository.sendGift(coupleId, gift)
            _uiState.value = _uiState.value.copy(isLoading = false, isSendingGift = false)
        }
    }

    fun openGift(coupleId: String, giftId: String) {
        viewModelScope.launch {
            giftRepository.openGift(coupleId, giftId)
        }
    }

    fun sendNote(coupleId: String, fromUserId: String, content: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val note = HandwrittenNote(fromUserId = fromUserId, content = content)
            giftRepository.sendNote(coupleId, note)
            _uiState.value = _uiState.value.copy(isLoading = false, isWritingNote = false)
        }
    }

    fun toggleSendingGift() {
        _uiState.value = _uiState.value.copy(isSendingGift = !_uiState.value.isSendingGift)
    }

    fun toggleWritingNote() {
        _uiState.value = _uiState.value.copy(isWritingNote = !_uiState.value.isWritingNote)
    }
}
