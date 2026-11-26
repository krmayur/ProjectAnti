package com.ourworld.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ourworld.app.data.model.*
import com.ourworld.app.data.repository.AuthRepository
import com.ourworld.app.data.repository.CoupleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class HomeUiState(
    val home: Home? = null,
    val furniture: List<FurnitureItem> = emptyList(),
    val myAvatar: Avatar = Avatar(),
    val partnerAvatar: Avatar? = null,
    val myName: String = "",
    val partnerName: String = "",
    val isCustomizing: Boolean = false,
    val selectedFurniture: FurnitureItem? = null,
    val showFurnitureShop: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val coupleRepository: CoupleRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadHome(coupleId: String, userId: String, partnerId: String) {
        viewModelScope.launch {
            // Load user names
            val myUser = authRepository.getUserById(userId)
            val partnerUser = authRepository.getUserById(partnerId)
            _uiState.value = _uiState.value.copy(
                myName = myUser?.displayName ?: "You",
                partnerName = partnerUser?.displayName ?: "Partner"
            )
        }

        // Observe home
        viewModelScope.launch {
            coupleRepository.observeHome(coupleId).collect { home ->
                _uiState.value = _uiState.value.copy(home = home)
            }
        }

        // Observe furniture
        viewModelScope.launch {
            coupleRepository.observeFurniture(coupleId).collect { furniture ->
                _uiState.value = _uiState.value.copy(furniture = furniture)
            }
        }

        // Observe my avatar
        viewModelScope.launch {
            coupleRepository.observePartnerAvatar(userId).collect { avatar ->
                if (avatar != null) {
                    _uiState.value = _uiState.value.copy(myAvatar = avatar)
                }
            }
        }

        // Observe partner avatar
        viewModelScope.launch {
            coupleRepository.observePartnerAvatar(partnerId).collect { avatar ->
                _uiState.value = _uiState.value.copy(partnerAvatar = avatar)
            }
        }
    }

    fun updateWallpaper(coupleId: String, index: Int) {
        viewModelScope.launch {
            val home = _uiState.value.home?.copy(wallpaper = index) ?: return@launch
            coupleRepository.updateHome(coupleId, home)
        }
    }

    fun updateLighting(coupleId: String, index: Int) {
        viewModelScope.launch {
            val home = _uiState.value.home?.copy(lighting = index) ?: return@launch
            coupleRepository.updateHome(coupleId, home)
        }
    }

    fun addFurniture(coupleId: String, type: String, name: String, emoji: String, userId: String) {
        viewModelScope.launch {
            val item = FurnitureItem(
                id = UUID.randomUUID().toString(),
                type = type,
                name = name,
                emoji = emoji,
                placedBy = userId,
                positionX = (0.2f..0.8f).random(),
                positionY = (0.4f..0.8f).random()
            )
            coupleRepository.addFurniture(coupleId, item)
        }
    }

    fun moveFurniture(coupleId: String, itemId: String, x: Float, y: Float) {
        viewModelScope.launch {
            coupleRepository.updateFurniturePosition(coupleId, itemId, x, y)
        }
    }

    fun removeFurniture(coupleId: String, itemId: String) {
        viewModelScope.launch {
            coupleRepository.removeFurniture(coupleId, itemId)
        }
    }

    fun playAnimation(userId: String, animation: String) {
        viewModelScope.launch {
            coupleRepository.updateAvatarAnimation(userId, animation)
        }
    }

    fun saveAvatar(userId: String, avatar: Avatar) {
        viewModelScope.launch {
            coupleRepository.updateAvatar(userId, avatar.copy(ownerId = userId))
        }
    }

    fun moveAvatar(userId: String, x: Float, y: Float) {
        _uiState.value = _uiState.value.copy(
            myAvatar = _uiState.value.myAvatar.copy(positionX = x, positionY = y)
        )
        viewModelScope.launch {
            coupleRepository.updateAvatarPosition(userId, x, y)
        }
    }

    fun toggleCustomizing() {
        _uiState.value = _uiState.value.copy(isCustomizing = !_uiState.value.isCustomizing)
    }

    fun toggleFurnitureShop() {
        _uiState.value = _uiState.value.copy(showFurnitureShop = !_uiState.value.showFurnitureShop)
    }

    private fun ClosedFloatingPointRange<Float>.random(): Float {
        return start + Math.random().toFloat() * (endInclusive - start)
    }
}
