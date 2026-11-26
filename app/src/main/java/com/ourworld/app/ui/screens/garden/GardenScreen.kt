package com.ourworld.app.ui.screens.garden

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourworld.app.data.model.*
import com.ourworld.app.ui.components.EmojiButton
import com.ourworld.app.ui.components.EmptyStateMessage
import com.ourworld.app.ui.components.FloatingHearts
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
    onMovePlant: (String, Float, Float) -> Unit,
    onHarvestPlant: (Plant) -> Unit,
    onTogglePlanting: () -> Unit
) {
    val seasonColors = when (uiState.currentSeason) {
        Season.SPRING -> listOf(Color(0xFFD8F3DC), Color(0xFFE9F5DB))
        Season.SUMMER -> listOf(Color(0xFFBDE0FE), Color(0xFFFFF8D6))
        Season.AUTUMN -> listOf(Color(0xFFFFE5D9), Color(0xFFFFD6A5))
        Season.WINTER -> listOf(Color(0xFFD7E9F7), Color(0xFFF0F7FF))
    }

    // Live ticker so growth stages & countdown update automatically over the 24h cycle
    var nowMs by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000L)
            nowMs = System.currentTimeMillis()
        }
    }

    var selectedPlantId by remember { mutableStateOf<String?>(null) }
    val selectedPlant = uiState.plants.find { it.id == selectedPlantId }

    var actionToast by remember { mutableStateOf<String?>(null) }
    var showSparkles by remember { mutableStateOf(false) }

    LaunchedEffect(actionToast) {
        if (actionToast != null) {
            delay(1800)
            actionToast = null
        }
    }
    LaunchedEffect(showSparkles) {
        if (showSparkles) {
            delay(1800)
            showSparkles = false
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

            // 2D Interactive Garden Scene
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val gardenW = constraints.maxWidth.toFloat().coerceAtLeast(1f)
                val gardenH = constraints.maxHeight.toFloat().coerceAtLeast(1f)

                GardenBackdrop(season = uiState.currentSeason)
                SeasonalParticles(season = uiState.currentSeason)

                if (uiState.plants.isEmpty()) {
                    EmptyStateMessage(
                        emoji = "🌱",
                        title = "Your garden awaits!",
                        subtitle = "Tap + to plant a seed together — it blooms from seed to flower in 1 day 💕",
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    uiState.plants
                        .sortedBy { it.positionY }
                        .forEach { plant ->
                            PlantOnGarden(
                                plant = plant,
                                nowMs = nowMs,
                                gardenW = gardenW,
                                gardenH = gardenH,
                                isSelected = plant.id == selectedPlantId,
                                onSelect = {
                                    selectedPlantId = if (selectedPlantId == plant.id) null else plant.id
                                },
                                onMove = { x, y -> onMovePlant(plant.id, x, y) }
                            )
                        }
                }

                if (showSparkles) {
                    FloatingHearts()
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = actionToast != null,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = OurWorldSoftWhite.copy(alpha = 0.94f),
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            actionToast ?: "",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OurWorldDarkBrown,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Interactive Plant Inspector Card
            AnimatedVisibility(visible = selectedPlant != null && !uiState.isPlanting) {
                selectedPlant?.let { plant ->
                    val stage = plant.computedStage(nowMs)
                    val progress = plant.computedProgress(nowMs)
                    val remaining = plant.timeRemainingLabel(nowMs)

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = OurWorldSoftWhite,
                        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
                        tonalElevation = 6.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        if (stage == GrowthStage.FULLY_GROWN || stage == GrowthStage.BLOOMING)
                                            plant.type.emoji else stage.emoji,
                                        fontSize = 30.sp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            plant.name,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = OurWorldDarkBrown
                                        )
                                        Text(
                                            "${stage.label} • $remaining",
                                            fontSize = 12.sp,
                                            color = OurWorldRose,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        onRemovePlant(plant.id)
                                        selectedPlantId = null
                                    }
                                ) {
                                    Icon(
                                        Icons.Outlined.Delete,
                                        contentDescription = "Remove Plant",
                                        tint = OurWorldWarmBrown
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Growth progress bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Growth", fontSize = 11.sp, color = OurWorldWarmBrown)
                                Text("${(progress * 100).toInt()}%", fontSize = 11.sp, color = OurWorldDarkBrown, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = OurWorldSuccess,
                                trackColor = OurWorldLightGray
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Interactive Care / Harvest Buttons
                            if (stage == GrowthStage.FULLY_GROWN) {
                                Button(
                                    onClick = {
                                        onHarvestPlant(plant)
                                        selectedPlantId = null
                                        showSparkles = true
                                        actionToast = "🌾 Harvested ${plant.name}! +${plant.type.harvestCoins} 🪙 +${plant.type.harvestXp} XP"
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = OurWorldGold)
                                ) {
                                    Text(
                                        "🌾 Harvest (+${plant.type.harvestCoins} 🪙, +${plant.type.harvestXp} XP)",
                                        color = OurWorldDarkBrown,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            onWaterPlant(plant.id)
                                            nowMs = System.currentTimeMillis()
                                            actionToast = "💧 Watered ${plant.name}! (+1.5h growth)"
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = OurWorldSky)
                                    ) {
                                        Text("💧 Water", color = OurWorldDarkBrown, fontWeight = FontWeight.SemiBold)
                                    }
                                    Button(
                                        onClick = {
                                            showSparkles = true
                                            actionToast = "☀️ Sent sunshine & love to ${plant.name}!"
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = OurWorldPeach)
                                    ) {
                                        Text("☀️ Sunshine", color = OurWorldDarkBrown, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Planting panel
            AnimatedVisibility(visible = uiState.isPlanting) {
                PlantingPanel(
                    onPlant = { type ->
                        onPlantSeed(type)
                        actionToast = "🫘 Planted ${type.label}! Blooms in ${type.growHours}h"
                    },
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
            moveTo(0f, h * 0.50f)
            quadraticTo(w * 0.35f, h * 0.38f, w * 0.70f, h * 0.48f)
            quadraticTo(w * 0.88f, h * 0.52f, w, h * 0.46f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(backHill, color = hillColor1)

        // Wooden fence posts along hill
        val fenceColor = Color(0xFFDDB892)
        val postCount = 8
        drawRect(fenceColor, topLeft = Offset(0f, h * 0.48f), size = Size(w, 8f))
        for (i in 0..postCount) {
            val px = (w / postCount) * i - 6f
            drawRoundRect(
                color = fenceColor,
                topLeft = Offset(px, h * 0.43f),
                size = Size(14f, h * 0.09f),
                cornerRadius = CornerRadius(4f, 4f)
            )
        }

        // Foreground garden meadow
        val frontHill = Path().apply {
            moveTo(0f, h * 0.56f)
            quadraticTo(w * 0.5f, h * 0.49f, w, h * 0.58f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(frontHill, color = hillColor2)
    }
}

@Composable
private fun SeasonalParticles(season: Season) {
    val particle = when (season) {
        Season.SPRING -> "🦋"
        Season.SUMMER -> "✨"
        Season.AUTUMN -> "🍂"
        Season.WINTER -> "❄️"
    }
    val particles = remember { List(4) { it } }
    Box(modifier = Modifier.fillMaxSize()) {
        particles.forEach { idx ->
            val inf = rememberInfiniteTransition(label = "p_$idx")
            val xFrac by inf.animateFloat(
                initialValue = 0.12f + idx * 0.22f,
                targetValue = 0.22f + idx * 0.20f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3200 + idx * 500, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "px_$idx"
            )
            val yFrac by inf.animateFloat(
                initialValue = 0.18f + (idx % 2) * 0.10f,
                targetValue = 0.34f + (idx % 2) * 0.08f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2600 + idx * 400, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "py_$idx"
            )
            Text(
                particle,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.TopStart)
                    .offset(x = (xFrac * 340).dp, y = (yFrac * 420).dp)
                    .alpha(0.75f)
            )
        }
    }
}

@Composable
private fun PlantOnGarden(
    plant: Plant,
    nowMs: Long,
    gardenW: Float,
    gardenH: Float,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onMove: (Float, Float) -> Unit
) {
    val stage = plant.computedStage(nowMs)
    val progress = plant.computedProgress(nowMs)

    val sway by rememberInfiniteTransition(label = "plant_${plant.id}").animateFloat(
        initialValue = 0.97f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "plant_sway"
    )

    val maxX = (gardenW - 180f).coerceAtLeast(100f)
    val minY = gardenH * 0.45f
    val maxY = (gardenH - 220f).coerceAtLeast(minY + 40f)

    var offset by remember(plant.id, plant.positionX, plant.positionY, maxX, maxY) {
        mutableStateOf(
            Offset(
                (plant.positionX.coerceIn(0.06f, 0.90f) * maxX),
                (minY + plant.positionY.coerceIn(0.10f, 0.88f) * (maxY - minY))
            )
        )
    }

    val flowerColor = when (plant.type) {
        PlantType.ROSE -> OurWorldRose
        PlantType.SUNFLOWER -> Color(0xFFFFC300)
        PlantType.TULIP -> Color(0xFFFF758F)
        PlantType.DAISY -> OurWorldSoftWhite
        PlantType.CHERRY_BLOSSOM -> Color(0xFFFFB3C6)
        PlantType.CACTUS -> Color(0xFF52B788)
        PlantType.LAVENDER -> OurWorldLavender
        PlantType.FERN -> Color(0xFF40916C)
        PlantType.BONSAI -> Color(0xFF52B788)
        PlantType.LOVE_TREE -> OurWorldRose
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .pointerInput(plant.id, maxX, maxY) {
                detectDragGestures(
                    onDragEnd = {
                        val normX = (offset.x / maxX).coerceIn(0.06f, 0.90f)
                        val normY = ((offset.y - minY) / (maxY - minY)).coerceIn(0.10f, 0.88f)
                        onMove(normX, normY)
                    }
                ) { change, dragAmount ->
                    change.consume()
                    offset = Offset(
                        (offset.x + dragAmount.x).coerceIn(0f, maxX),
                        (offset.y + dragAmount.y).coerceIn(minY, maxY)
                    )
                }
            }
            .clickable(onClick = onSelect)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // 2D Canvas Plant & Terracotta Pot
            Canvas(
                modifier = Modifier
                    .size(72.dp)
                    .scale(if (isSelected) 1.12f else sway)
            ) {
                val w = size.width
                val h = size.height

                // Shadow
                drawOval(Color.Black.copy(alpha = 0.12f), Offset(w * 0.22f, h * 0.90f), Size(w * 0.56f, h * 0.08f))

                // Terracotta pot
                val pot = Path().apply {
                    moveTo(w * 0.28f, h * 0.66f)
                    lineTo(w * 0.72f, h * 0.66f)
                    lineTo(w * 0.65f, h * 0.92f)
                    lineTo(w * 0.35f, h * 0.92f)
                    close()
                }
                drawPath(pot, color = Color(0xFFD97757))
                drawRoundRect(Color(0xFFE08B6D), Offset(w * 0.24f, h * 0.62f), Size(w * 0.52f, h * 0.08f), CornerRadius(6f, 6f))

                when (stage) {
                    GrowthStage.SEED -> {
                        // Soil mound + tiny seed
                        drawOval(Color(0xFF6B4423), Offset(w * 0.32f, h * 0.56f), Size(w * 0.36f, h * 0.10f))
                        drawCircle(Color(0xFFA67C52), radius = w * 0.055f, center = Offset(w * 0.50f, h * 0.57f))
                    }
                    GrowthStage.SPROUT -> {
                        drawLine(Color(0xFF52B788), Offset(w * 0.5f, h * 0.62f), Offset(w * 0.5f, h * 0.44f), strokeWidth = 5f, cap = StrokeCap.Round)
                        drawOval(Color(0xFF74C69D), Offset(w * 0.34f, h * 0.38f), Size(w * 0.18f, h * 0.11f))
                        drawOval(Color(0xFF74C69D), Offset(w * 0.48f, h * 0.38f), Size(w * 0.18f, h * 0.11f))
                    }
                    GrowthStage.GROWING -> {
                        drawLine(Color(0xFF40916C), Offset(w * 0.5f, h * 0.62f), Offset(w * 0.5f, h * 0.30f), strokeWidth = 6f, cap = StrokeCap.Round)
                        drawOval(Color(0xFF52B788), Offset(w * 0.26f, h * 0.40f), Size(w * 0.24f, h * 0.14f))
                        drawOval(Color(0xFF52B788), Offset(w * 0.50f, h * 0.36f), Size(w * 0.24f, h * 0.14f))
                        drawCircle(flowerColor.copy(alpha = 0.7f), radius = w * 0.08f, center = Offset(w * 0.50f, h * 0.28f))
                    }
                    GrowthStage.BLOOMING, GrowthStage.FULLY_GROWN -> {
                        drawLine(Color(0xFF2D6A4F), Offset(w * 0.5f, h * 0.62f), Offset(w * 0.5f, h * 0.26f), strokeWidth = 6f, cap = StrokeCap.Round)
                        drawOval(Color(0xFF52B788), Offset(w * 0.24f, h * 0.42f), Size(w * 0.24f, h * 0.14f))
                        drawOval(Color(0xFF52B788), Offset(w * 0.52f, h * 0.38f), Size(w * 0.24f, h * 0.14f))

                        val r = if (stage == GrowthStage.FULLY_GROWN) w * 0.11f else w * 0.085f
                        val cx = w * 0.50f
                        val cy = h * 0.24f
                        // Petals around center
                        for (a in 0 until 5) {
                            val angle = Math.toRadians((a * 72).toDouble())
                            val px = cx + (r * 0.95f * kotlin.math.cos(angle)).toFloat()
                            val py = cy + (r * 0.95f * kotlin.math.sin(angle)).toFloat()
                            drawCircle(flowerColor, radius = r, center = Offset(px, py))
                        }
                        drawCircle(Color(0xFFFFD54F), radius = r * 0.75f, center = Offset(cx, cy))
                    }
                }
            }

            // Name & live growth/water pill
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) OurWorldPeach else OurWorldSoftWhite.copy(alpha = 0.88f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        if (stage == GrowthStage.FULLY_GROWN) "${plant.name} 🌾" else "${plant.name} ${(progress * 100).toInt()}%",
                        fontSize = 10.sp,
                        color = OurWorldDarkBrown,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .width(48.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = OurWorldSuccess,
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
                "Choose a Seed 🌱",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = OurWorldDarkBrown
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(PlantType.entries.toList()) { plantType ->
                    EmojiButton(
                        emoji = plantType.emoji,
                        label = "${plantType.label} (${plantType.growHours}h)",
                        onClick = { if (!isLoading) onPlant(plantType) }
                    )
                }
            }
        }
    }
}
