package com.ourworld.app.ui.screens.garden

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourworld.app.data.model.*
import com.ourworld.app.ui.components.CozyCard
import com.ourworld.app.ui.components.EmojiButton
import com.ourworld.app.ui.components.EmptyStateMessage
import com.ourworld.app.ui.components.SectionHeader
import com.ourworld.app.ui.theme.*
import com.ourworld.app.ui.viewmodel.GardenUiState

@Composable
fun GardenScreen(
    uiState: GardenUiState,
    coupleId: String,
    userId: String,
    onPlantSeed: (PlantType) -> Unit,
    onWaterPlant: (String) -> Unit,
    onRemovePlant: (String) -> Unit,
    onTogglePlanting: () -> Unit
) {
    val seasonColors = when (uiState.currentSeason) {
        Season.SPRING -> listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9), Color(0xFFA5D6A7))
        Season.SUMMER -> listOf(Color(0xFFFFF9C4), Color(0xFFFFF59D), Color(0xFFFFF176))
        Season.AUTUMN -> listOf(Color(0xFFFFE0B2), Color(0xFFFFCC80), Color(0xFFFFB74D))
        Season.WINTER -> listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB), Color(0xFF90CAF9))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(seasonColors)
                )
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Our Garden 🌱",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = OurWorldDarkBrown
                    )
                    Text(
                        "${uiState.currentSeason.emoji} ${uiState.currentSeason.label} • ${uiState.plants.size} plants",
                        fontSize = 12.sp,
                        color = OurWorldWarmBrown
                    )
                }
                FloatingActionButton(
                    onClick = onTogglePlanting,
                    containerColor = OurWorldMint,
                    contentColor = OurWorldDarkBrown,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Add, "Plant")
                }
            }

            // Garden area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp)
            ) {
                if (uiState.plants.isEmpty()) {
                    EmptyStateMessage(
                        emoji = "🌱",
                        title = "Your garden awaits!",
                        subtitle = "Plant your first seed together and watch it grow 💕",
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    // Ground
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.4f)
                            .align(Alignment.BottomCenter)
                            .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                            .background(Color(0xFF8B6F47).copy(alpha = 0.3f))
                    )

                    // Plants
                    uiState.plants.forEach { plant ->
                        PlantOnGarden(
                            plant = plant,
                            onWater = { onWaterPlant(plant.id) },
                            onRemove = { onRemovePlant(plant.id) }
                        )
                    }
                }
            }

            // Planting panel
            AnimatedVisibility(visible = uiState.isPlanting) {
                PlantingPanel(
                    onPlant = onPlantSeed,
                    isLoading = uiState.isLoading
                )
            }
        }
    }
}

@Composable
private fun PlantOnGarden(
    plant: Plant,
    onWater: () -> Unit,
    onRemove: () -> Unit
) {
    val bounce by rememberInfiniteTransition(label = "plant_${plant.id}").animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "plant_bounce"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
            .offset(
                x = (plant.positionX * 300).dp,
                y = (plant.positionY * 400).dp
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable(onClick = onWater)
        ) {
            // Plant emoji based on stage
            Text(
                text = when (plant.stage) {
                    GrowthStage.SEED -> plant.stage.emoji
                    GrowthStage.SPROUT -> plant.stage.emoji
                    GrowthStage.GROWING -> "🪴"
                    GrowthStage.BLOOMING -> plant.type.emoji
                    GrowthStage.FULLY_GROWN -> plant.type.emoji
                },
                fontSize = when (plant.stage) {
                    GrowthStage.SEED -> 20.sp
                    GrowthStage.SPROUT -> 24.sp
                    GrowthStage.GROWING -> 28.sp
                    GrowthStage.BLOOMING -> 32.sp
                    GrowthStage.FULLY_GROWN -> 36.sp
                },
                modifier = Modifier.scale(bounce)
            )
            // Water level indicator
            if (plant.waterLevel < 50) {
                Text("💧", fontSize = 12.sp)
            }
            Text(
                plant.name,
                fontSize = 10.sp,
                color = OurWorldDarkBrown,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun PlantingPanel(
    onPlant: (PlantType) -> Unit,
    isLoading: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = OurWorldSoftWhite,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Choose a Plant 🌱",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = OurWorldDarkBrown
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(PlantType.entries.toList()) { plantType ->
                    EmojiButton(
                        emoji = plantType.emoji,
                        label = "${plantType.label}\n${plantType.growDays}d",
                        onClick = { if (!isLoading) onPlant(plantType) }
                    )
                }
            }
        }
    }
}
