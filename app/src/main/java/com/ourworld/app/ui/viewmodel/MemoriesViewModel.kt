package com.ourworld.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ourworld.app.data.model.Achievement
import com.ourworld.app.data.model.Memory
import com.ourworld.app.data.model.MemoryType
import com.ourworld.app.data.model.Milestone
import com.ourworld.app.data.repository.MemoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MemoriesUiState(
    val memories: List<Memory> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
    val milestones: List<Milestone> = emptyList(),
    val anniversaryDate: Long? = null,
    val isAddingMemory: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class MemoriesViewModel @Inject constructor(
    private val memoryRepository: MemoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemoriesUiState())
    val uiState: StateFlow<MemoriesUiState> = _uiState.asStateFlow()

    fun loadMemories(coupleId: String) {
        viewModelScope.launch {
            memoryRepository.observeMemories(coupleId).collect { memories ->
                _uiState.value = _uiState.value.copy(memories = memories)
            }
        }
        viewModelScope.launch {
            memoryRepository.observeAchievements(coupleId).collect { achievements ->
                _uiState.value = _uiState.value.copy(achievements = achievements)
            }
        }
        viewModelScope.launch {
            memoryRepository.observeMilestones(coupleId).collect { milestones ->
                _uiState.value = _uiState.value.copy(milestones = milestones)
            }
        }
        viewModelScope.launch {
            memoryRepository.observeAnniversaryDate(coupleId).collect { date ->
                _uiState.value = _uiState.value.copy(anniversaryDate = date)
            }
        }
    }

    fun setAnniversaryDate(coupleId: String, timestamp: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(anniversaryDate = timestamp)
            memoryRepository.updateAnniversaryDate(coupleId, timestamp)
        }
    }

    fun addMemory(coupleId: String, title: String, description: String, type: MemoryType, userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val memory = Memory(
                coupleId = coupleId,
                title = title,
                description = description,
                type = type,
                addedBy = userId,
                emoji = type.emoji
            )
            memoryRepository.addMemory(coupleId, memory)
            _uiState.value = _uiState.value.copy(isLoading = false, isAddingMemory = false)
        }
    }

    fun toggleAddingMemory() {
        _uiState.value = _uiState.value.copy(isAddingMemory = !_uiState.value.isAddingMemory)
    }
}
