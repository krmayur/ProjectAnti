package com.ourworld.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ourworld.app.data.model.User
import com.ourworld.app.data.repository.AuthRepository
import com.ourworld.app.data.repository.CoupleRepository
import com.ourworld.app.data.repository.MemoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isPaired: Boolean = false,
    val currentUser: User? = null,
    val error: String? = null,
    val pairingCode: String = "",
    val pairingSuccess: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val coupleRepository: CoupleRepository,
    private val memoryRepository: MemoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkLoginState()
    }

    private fun checkLoginState() {
        if (authRepository.isLoggedIn) {
            viewModelScope.launch {
                authRepository.observeCurrentUser().collect { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoggedIn = true,
                        isPaired = user?.isPaired == true,
                        currentUser = user,
                        pairingCode = user?.pairingCode ?: ""
                    )
                }
            }
        }
    }

    fun register(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.register(email, password, displayName)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        currentUser = user,
                        pairingCode = user.pairingCode
                    )
                    checkLoginState()
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Registration failed"
                    )
                }
            )
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.login(email, password)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        isPaired = user.isPaired,
                        currentUser = user,
                        pairingCode = user.pairingCode
                    )
                    checkLoginState()
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Login failed"
                    )
                }
            )
        }
    }

    fun pairWithCode(code: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = coupleRepository.pairWithCode(code.uppercase().trim())
            result.fold(
                onSuccess = { coupleId ->
                    // Initialize achievements and milestones for the new couple
                    memoryRepository.initializeDefaultAchievements(coupleId)
                    memoryRepository.initializeDefaultMilestones(coupleId)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isPaired = true,
                        pairingSuccess = true
                    )
                    checkLoginState()
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Pairing failed"
                    )
                }
            )
        }
    }

    fun unpair() {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = coupleRepository.unpair(user.uid, user.partnerId)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isPaired = false,
                        pairingSuccess = false
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to unpair"
                    )
                }
            )
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = AuthUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
