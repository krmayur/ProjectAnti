package com.ourworld.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ourworld.app.data.model.Plant
import com.ourworld.app.data.model.PlantType
import com.ourworld.app.data.model.Season
import com.ourworld.app.data.repository.GardenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class GardenUiState(
    val plants: List<Plant> = emptyList(),
    val currentSeason: Season = Season.SPRING,
    val isPlanting: Boolean = false,
    val selectedPlantType: PlantType? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class GardenViewModel @Inject constructor(
    private val gardenRepository: GardenRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GardenUiState(currentSeason = getCurrentSeason()))
    val uiState: StateFlow<GardenUiState> = _uiState.asStateFlow()

    fun loadGarden(coupleId: String) {
        viewModelScope.launch {
            gardenRepository.observePlants(coupleId).collect { plants ->
                _uiState.value = _uiState.value.copy(plants = plants)
            }
        }
    }

    fun plantSeed(coupleId: String, plantType: PlantType, userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val x = (0.1f..0.9f).random()
            val y = (0.3f..0.8f).random()
            gardenRepository.plantSeed(coupleId, plantType, userId, x, y)
            _uiState.value = _uiState.value.copy(isLoading = false, isPlanting = false)
        }
    }

    fun waterPlant(coupleId: String, plantId: String) {
        viewModelScope.launch {
            gardenRepository.waterPlant(coupleId, plantId)
        }
    }

    fun removePlant(coupleId: String, plantId: String) {
        viewModelScope.launch {
            gardenRepository.removePlant(coupleId, plantId)
        }
    }

    fun movePlant(coupleId: String, plantId: String, x: Float, y: Float) {
        viewModelScope.launch {
            gardenRepository.updatePlantPosition(coupleId, plantId, x, y)
        }
    }

    fun togglePlanting() {
        _uiState.value = _uiState.value.copy(isPlanting = !_uiState.value.isPlanting)
    }

    private fun getCurrentSeason(): Season {
        val month = Calendar.getInstance().get(Calendar.MONTH)
        return when (month) {
            in 2..4 -> Season.SPRING
            in 5..7 -> Season.SUMMER
            in 8..10 -> Season.AUTUMN
            else -> Season.WINTER
        }
    }

    private fun ClosedFloatingPointRange<Float>.random(): Float {
        return start + Math.random().toFloat() * (endInclusive - start)
    }
}
