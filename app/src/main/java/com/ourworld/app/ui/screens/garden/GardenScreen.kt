package com.ourworld.app.ui.screens.garden

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourworld.app.data.model.*
import com.ourworld.app.ui.components.EmojiButton
import com.ourworld.app.ui.components.EmptyStateMessage
import com.ourworld.app.ui.theme.*
import com.ourworld.app.ui.viewmodel.GardenUiState
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

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
        Season.SPRING -> listOf(Color(0xFFD8F3DC), Color(0xFFE9F5DB))
        Season.SUMMER -> listOf(Color(0xFFBDE0FE), Color(0xFFFFF8D6))
        Season.AUTUMN -> listOf(Color(0xFFFFE5D9), Color(0xFFFFD6A5))
        Season.WINTER -> listOf(Color(0xFFD7E9F7), Color(0xFFF0F7FF))
    }

    var wateredToast by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(wateredToast) {
        if (wateredToast != null) {
            delay(1500)
            wateredToast = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(seasonColors))
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
                        "${uiState.currentSeason.emoji} ${uiState.currentSeason.label} • ${uiState.plants.size} plants (tap to water 💧)",
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

            // 2D Garden scene
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val gardenW = constraints.maxWidth.toFloat().coerceAtLeast(1f)
                val gardenH = constraints.maxHeight.toFloat().coerceAtLeast(1f)

                GardenBackdrop(season = uiState.currentSeason)

                if (uiState.plants.isEmpty()) {
                    EmptyStateMessage(
                        emoji = "🌱",
                        title = "Your garden awaits!",
                        subtitle = "Tap + to plant your first seed together and watch it bloom 💕",
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    uiState.plants.forEach { plant ->
                        PlantOnGarden(
                            plant = plant,
                            gardenW = gardenW,
                            gardenH = gardenH,
                            onWater = {
                                onWaterPlant(plant.id)
                                wateredToast = "💧 Watered ${plant.name}!"
                            }
                        )
                    }
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = wateredToast != null,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = OurWorldSoftWhite.copy(alpha = 0.92f),
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            wateredToast ?: "",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OurWorldDarkBrown,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
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
private fun GardenBackdrop(season: Season) {
    val hillColor1 = when (season) {
        Season.SPRING -> Color(0xFFB7E4C7)
        Season.SUMMER -> Color(0xFF95D5B2)
        Season.AUTUMN -> Color(0xFFE9C46A).copy(alpha = 0.7f)
        Season.WINTER -> Color(0xFFE0EEF8)
    }
    val hillColor2 = when (season) {
        Season.SPRING -> Color(0xFF95D5B2)
        Season.SUMMER -> Color(0xFF74C69D)
        Season.AUTUMN -> Color(0xFFDDA15E).copy(alpha = 0.75f)
        Season.WINTER -> Color(0xFFCDE2F2)
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Sun
        drawCircle(
            color = Color(0xFFFFE082).copy(alpha = 0.45f),
            radius = w * 0.14f,
            center = Offset(w * 0.82f, h * 0.14f)
        )
        drawCircle(
            color = Color(0xFFFFD54F),
            radius = w * 0.085f,
            center = Offset(w * 0.82f, h * 0.14f)
        )

        // Soft clouds
        val cloudColor = Color.White.copy(alpha = 0.75f)
        drawCircle(cloudColor, w * 0.07f, Offset(w * 0.22f, h * 0.15f))
        drawCircle(cloudColor, w * 0.09f, Offset(w * 0.30f, h * 0.14f))
        drawCircle(cloudColor, w * 0.06f, Offset(w * 0.38f, h * 0.16f))

        // Rolling back hill
        val backHill = Path().apply {
            moveTo(0f, h * 0.56f)
            quadraticBezierTo(w * 0.35f, h * 0.44f, w * 0.70f, h * 0.54f)
            quadraticBezierTo(w * 0.88f, h * 0.58f, w, h * 0.52f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(backHill, color = hillColor1)

        // Wooden fence posts along hill
        val fenceColor = Color(0xFFDDB892)
        val postCount = 8
        drawRect(fenceColor, topLeft = Offset(0f, h * 0.54f), size = Size(w, 8f))
        for (i in 0..postCount) {
            val px = (w / postCount) * i - 6f
            drawRoundRect(
                color = fenceColor,
                topLeft = Offset(px, h * 0.49f),
                size = Size(14f, h * 0.09f),
                cornerRadius = CornerRadius(4f, 4f)
            )
        }

        // Foreground garden meadow
        val frontHill = Path().apply {
            moveTo(0f, h * 0.62f)
            quadraticBezierTo(w * 0.5f, h * 0.55f, w, h * 0.64f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(frontHill, color = hillColor2)
    }
}

@Composable
private fun PlantOnGarden(
    plant: Plant,
    gardenW: Float,
    gardenH: Float,
    onWater: () -> Unit
) {
    val bounce by rememberInfiniteTransition(label = "plant_${plant.id}").animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "plant_bounce"
    )

    val maxX = (gardenW - 180f).coerceAtLeast(100f)
    val minY = gardenH * 0.50f
    val maxY = (gardenH - 180f).coerceAtLeast(minY + 40f)
    val xPx = (plant.positionX.coerceIn(0.08f, 0.88f) * maxX).roundToInt()
    val yPx = (minY + plant.positionY.coerceIn(0.1f, 0.85f) * (maxY - minY)).roundToInt()

    Box(
        modifier = Modifier
            .offset { IntOffset(xPx, yPx) }
            .clickable(onClick = onWater)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = when (plant.stage) {
                    GrowthStage.SEED -> plant.stage.emoji
                    GrowthStage.SPROUT -> plant.stage.emoji
                    GrowthStage.GROWING -> "🪴"
                    GrowthStage.BLOOMING -> plant.type.emoji
                    GrowthStage.FULLY_GROWN -> plant.type.emoji
                },
                fontSize = when (plant.stage) {
                    GrowthStage.SEED -> 24.sp
                    GrowthStage.SPROUT -> 28.sp
                    GrowthStage.GROWING -> 34.sp
                    GrowthStage.BLOOMING -> 38.sp
                    GrowthStage.FULLY_GROWN -> 44.sp
                },
                modifier = Modifier.scale(bounce)
            )
            // Soil / pot pill + water bar
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = OurWorldSoftWhite.copy(alpha = 0.85f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        plant.name,
                        fontSize = 10.sp,
                        color = OurWorldDarkBrown,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    LinearProgressIndicator(
                        progress = { (plant.waterLevel / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .width(44.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = OurWorldInfo,
                        trackColor = OurWorldLightGray
                    )
                }
            }
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
