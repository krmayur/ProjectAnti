package com.ourworld.app.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.MusicOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourworld.app.data.model.*
import com.ourworld.app.ui.components.*
import com.ourworld.app.ui.theme.*
import com.ourworld.app.ui.viewmodel.HomeUiState
import com.ourworld.app.util.AmbientSoundManager
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    coupleId: String,
    userId: String,
    userCoins: Int,
    onUpdateWallpaper: (Int) -> Unit,
    onUpdateLighting: (Int) -> Unit,
    onAddFurniture: (String, String, String) -> Unit,
    onMoveFurniture: (String, Float, Float) -> Unit,
    onRemoveFurniture: (String) -> Unit,
    onPlayAnimation: (String) -> Unit,
    onToggleCustomize: () -> Unit,
    onNavigateToAvatar: () -> Unit,
    onUnpair: () -> Unit,
    onLogout: () -> Unit
) {
    val wallpaper = Wallpaper.entries.getOrElse(uiState.home?.wallpaper ?: 0) { Wallpaper.CREAM }
    val lighting = Lighting.entries.getOrElse(uiState.home?.lighting ?: 0) { Lighting.WARM }
    val isDarkWall = wallpaper == Wallpaper.STARRY || lighting == Lighting.MOONLIGHT
    val headerTextColor = if (isDarkWall) OurWorldSoftWhite else OurWorldDarkBrown
    val subHeaderColor = if (isDarkWall) OurWorldLightRose else OurWorldWarmBrown

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showUnpairConfirm by remember { mutableStateOf(false) }
    var showHeartBurst by remember { mutableStateOf(false) }
    var tappedFurnitureTip by remember { mutableStateOf<String?>(null) }

    val soundManager = remember { AmbientSoundManager() }
    var isMusicPlaying by remember { mutableStateOf(false) }
    DisposableEffect(Unit) {
        onDispose { soundManager.stop() }
    }

    LaunchedEffect(showHeartBurst) {
        if (showHeartBurst) {
            delay(1800)
            showHeartBurst = false
        }
    }

    LaunchedEffect(tappedFurnitureTip) {
        if (tappedFurnitureTip != null) {
            delay(2000)
            tappedFurnitureTip = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(wallpaper.color))
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Our Home 🏠",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = headerTextColor
                    )
                    Text(
                        "${lighting.emoji} ${lighting.label}",
                        fontSize = 12.sp,
                        color = subHeaderColor
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CoinDisplay(coins = userCoins)
                    IconButton(onClick = { isMusicPlaying = soundManager.toggle() }) {
                        Icon(
                            if (isMusicPlaying) Icons.Outlined.MusicNote else Icons.Outlined.MusicOff,
                            contentDescription = "Ambient Music",
                            tint = if (isMusicPlaying) OurWorldRose else subHeaderColor
                        )
                    }
                    IconButton(onClick = onToggleCustomize) {
                        Icon(
                            if (uiState.isCustomizing) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (uiState.isCustomizing) "Done Customizing" else "Customize Home",
                            tint = OurWorldRose
                        )
                    }
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = subHeaderColor
                        )
                    }
                }
            }

            // Customize mode hint pill
            AnimatedVisibility(visible = uiState.isCustomizing) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = OurWorldPeach.copy(alpha = 0.95f)
                ) {
                    Text(
                        "✨ Customize Mode: Drag furniture to arrange or tap ✕ to remove",
                        fontSize = 12.sp,
                        color = OurWorldDarkBrown,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp)
                    )
                }
            }

            // Room area with 2D illustration, furniture, and avatars
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val roomWidthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)
                val roomHeightPx = constraints.maxHeight.toFloat().coerceAtLeast(1f)

                // Cozy 2D Room Backdrop (Window, Fairy Lights, Baseboard, Rug)
                CozyRoomBackdrop(lighting = lighting)

                // Furniture items positioned proportionally to room size
                uiState.furniture.forEach { item ->
                    FurnitureOnCanvas(
                        item = item,
                        roomWidthPx = roomWidthPx,
                        roomHeightPx = roomHeightPx,
                        isEditing = uiState.isCustomizing,
                        onTap = {
                            tappedFurnitureTip = "${item.emoji} ${item.name}"
                        },
                        onMove = { x, y -> onMoveFurniture(item.id, x, y) },
                        onRemove = { onRemoveFurniture(item.id) }
                    )
                }

                // Avatars standing on the cozy rug
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 22.dp)
                ) {
                    AvatarPairDisplay(
                        myAvatar = uiState.myAvatar,
                        partnerAvatar = uiState.partnerAvatar,
                        myName = uiState.myName,
                        partnerName = uiState.partnerName,
                        avatarSize = 104,
                        onHeartClick = {
                            showHeartBurst = true
                            onPlayAnimation(AvatarAnimation.HEART.name)
                        },
                        onMyAvatarClick = onNavigateToAvatar
                    )
                }

                // Interactive floating heart burst when tapping the couple heart
                if (showHeartBurst) {
                    FloatingHearts()
                }

                // Furniture tap tooltip
                androidx.compose.animation.AnimatedVisibility(
                    visible = tappedFurnitureTip != null,
                    enter = fadeIn(),
                    exit = fadeOut(),
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
                            tappedFurnitureTip ?: "",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OurWorldDarkBrown,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Interaction buttons
            AnimationBar(
                currentAnimation = uiState.myAvatar.currentAnimation,
                onPlayAnimation = onPlayAnimation,
                onCustomizeAvatar = onNavigateToAvatar
            )

            // Customization panel
            AnimatedVisibility(visible = uiState.isCustomizing) {
                CustomizePanel(
                    onUpdateWallpaper = onUpdateWallpaper,
                    onUpdateLighting = onUpdateLighting,
                    onAddFurniture = onAddFurniture,
                    currentWallpaper = uiState.home?.wallpaper ?: 0,
                    currentLighting = uiState.home?.lighting ?: 0
                )
            }
        }
    }

    // Settings Dialog
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            containerColor = OurWorldSoftWhite,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    "Home Settings ⚙️",
                    fontWeight = FontWeight.Bold,
                    color = OurWorldDarkBrown
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = OurWorldCream,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Couple", fontSize = 12.sp, color = OurWorldWarmBrown)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "${uiState.myName} 💕 ${uiState.partnerName}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OurWorldDarkBrown
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            showSettingsDialog = false
                            showUnpairConfirm = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OurWorldError)
                    ) {
                        Text("Unpair from Partner 💔", fontWeight = FontWeight.SemiBold)
                    }

                    TextButton(
                        onClick = {
                            showSettingsDialog = false
                            onLogout()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Sign Out", color = OurWorldWarmBrown)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Close", color = OurWorldRose, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }

    // Unpair Confirmation Dialog
    if (showUnpairConfirm) {
        AlertDialog(
            onDismissRequest = { showUnpairConfirm = false },
            containerColor = OurWorldSoftWhite,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    "Unpair from ${uiState.partnerName}?",
                    fontWeight = FontWeight.Bold,
                    color = OurWorldDarkBrown
                )
            },
            text = {
                Text(
                    "Both of you will be returned to the pairing screen. You can pair again anytime using a pairing code.",
                    color = OurWorldWarmBrown,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showUnpairConfirm = false
                        onUnpair()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OurWorldError)
                ) {
                    Text("Unpair", color = OurWorldSoftWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnpairConfirm = false }) {
                    Text("Cancel", color = OurWorldWarmBrown)
                }
            }
        )
    }
}

@Composable
private fun CozyRoomBackdrop(lighting: Lighting) {
    val twinkle by rememberInfiniteTransition(label = "twinkle").animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "twinkle_alpha"
    )

    val windowSkyColors = when (lighting) {
        Lighting.MOONLIGHT -> listOf(Color(0xFF1A2547), Color(0xFF3B4D80))
        Lighting.SUNSET -> listOf(Color(0xFFFF8C7A), Color(0xFFFFD194))
        Lighting.ROMANTIC -> listOf(Color(0xFF6B4E8A), Color(0xFFD499B9))
        else -> listOf(Color(0xFFA8D8FF), Color(0xFFE3F4FF))
    }

    val ambientTint = when (lighting) {
        Lighting.WARM -> OurWorldGold.copy(alpha = 0.14f)
        Lighting.ROMANTIC -> OurWorldLavender.copy(alpha = 0.22f)
        Lighting.SUNSET -> Color(0xFFFF9E7D).copy(alpha = 0.18f)
        Lighting.MOONLIGHT -> Color(0xFF1D2B53).copy(alpha = 0.30f)
        Lighting.FAIRY -> OurWorldGold.copy(alpha = 0.10f)
        Lighting.BRIGHT -> Color.Transparent
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val floorTop = h * 0.63f

        // Arched Window in the center-back wall
        val winW = w * 0.34f
        val winH = h * 0.28f
        val winLeft = (w - winW) / 2f
        val winTop = h * 0.12f

        // Window sky
        drawRoundRect(
            brush = Brush.verticalGradient(windowSkyColors),
            topLeft = Offset(winLeft, winTop),
            size = Size(winW, winH),
            cornerRadius = CornerRadius(winW * 0.5f, winW * 0.5f)
        )
        // Sun or Moon inside window
        drawCircle(
            color = if (lighting == Lighting.MOONLIGHT || lighting == Lighting.ROMANTIC)
                Color(0xFFFFF6C3) else Color(0xFFFFE082),
            radius = winW * 0.11f,
            center = Offset(winLeft + winW * 0.7f, winTop + winH * 0.32f)
        )
        // Window frame & panes
        drawRoundRect(
            color = Color(0xFFD7B99E),
            topLeft = Offset(winLeft, winTop),
            size = Size(winW, winH),
            cornerRadius = CornerRadius(winW * 0.5f, winW * 0.5f),
            style = Stroke(width = 8f)
        )
        drawLine(
            color = Color(0xFFD7B99E),
            start = Offset(winLeft + winW / 2f, winTop),
            end = Offset(winLeft + winW / 2f, winTop + winH),
            strokeWidth = 5f
        )
        drawLine(
            color = Color(0xFFD7B99E),
            start = Offset(winLeft, winTop + winH * 0.55f),
            end = Offset(winLeft + winW, winTop + winH * 0.55f),
            strokeWidth = 5f
        )

        // Hanging fairy lights string across the top
        val strandPath = Path().apply {
            moveTo(0f, h * 0.03f)
            for (i in 1..4) {
                val segW = w / 4f
                val startX = (i - 1) * segW
                quadraticBezierTo(
                    startX + segW / 2f, h * 0.09f,
                    startX + segW, h * 0.03f
                )
            }
        }
        drawPath(strandPath, color = OurWorldWarmBrown.copy(alpha = 0.45f), style = Stroke(width = 3f))

        // Fairy light bulbs
        val bulbCount = 9
        for (i in 1 until bulbCount) {
            val bx = w * (i.toFloat() / bulbCount)
            val sag = sin((i.toFloat() / bulbCount) * 4f * Math.PI).toFloat()
            val by = h * 0.045f + kotlin.math.abs(sag) * h * 0.028f
            val bulbAlpha = if (i % 2 == 0) twinkle else (1.4f - twinkle).coerceIn(0.4f, 1f)
            drawCircle(
                color = Color(0xFFFFE082).copy(alpha = bulbAlpha * 0.45f),
                radius = 16f,
                center = Offset(bx, by)
            )
            drawCircle(
                color = Color(0xFFFFF8DC).copy(alpha = bulbAlpha),
                radius = 6f,
                center = Offset(bx, by)
            )
        }

        // Wooden baseboard
        drawRect(
            color = Color(0xFFE2C6AE),
            topLeft = Offset(0f, floorTop - 12f),
            size = Size(w, 12f)
        )

        // Warm wooden floor
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFF3DEC9), Color(0xFFE8CBB0)),
                startY = floorTop,
                endY = h
            ),
            topLeft = Offset(0f, floorTop),
            size = Size(w, h - floorTop)
        )

        // Cozy pastel oval rug under avatars
        drawOval(
            color = OurWorldLightRose.copy(alpha = 0.55f),
            topLeft = Offset(w * 0.14f, h * 0.70f),
            size = Size(w * 0.72f, h * 0.22f)
        )
        drawOval(
            color = OurWorldCream.copy(alpha = 0.65f),
            topLeft = Offset(w * 0.19f, h * 0.72f),
            size = Size(w * 0.62f, h * 0.18f),
            style = Stroke(width = 4f)
        )

        // Ambient lighting wash
        if (ambientTint != Color.Transparent) {
            drawRect(color = ambientTint, topLeft = Offset.Zero, size = size)
        }
    }
}

@Composable
private fun FurnitureOnCanvas(
    item: FurnitureItem,
    roomWidthPx: Float,
    roomHeightPx: Float,
    isEditing: Boolean,
    onTap: () -> Unit,
    onMove: (Float, Float) -> Unit,
    onRemove: () -> Unit
) {
    val maxX = (roomWidthPx - 140f).coerceAtLeast(100f)
    val maxY = (roomHeightPx - 180f).coerceAtLeast(100f)

    var offset by remember(item.id, item.positionX, item.positionY, maxX, maxY) {
        mutableStateOf(
            Offset(
                (item.positionX.coerceIn(0.05f, 0.9f) * maxX),
                (item.positionY.coerceIn(0.05f, 0.85f) * maxY)
            )
        )
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .then(
                if (isEditing) {
                    Modifier.pointerInput(item.id, maxX, maxY) {
                        detectDragGestures(
                            onDragEnd = {
                                onMove(
                                    (offset.x / maxX).coerceIn(0.05f, 0.95f),
                                    (offset.y / maxY).coerceIn(0.05f, 0.9f)
                                )
                            }
                        ) { change, dragAmount ->
                            change.consume()
                            offset = Offset(
                                (offset.x + dragAmount.x).coerceIn(0f, maxX),
                                (offset.y + dragAmount.y).coerceIn(0f, maxY)
                            )
                        }
                    }
                } else {
                    Modifier.clickable(onClick = onTap)
                }
            )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(item.emoji, fontSize = 38.sp)
            if (isEditing) {
                Text(
                    "✕",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = OurWorldError,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(OurWorldSoftWhite)
                        .border(1.dp, OurWorldError.copy(alpha = 0.4f), CircleShape)
                        .clickable(onClick = onRemove)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun AnimationBar(
    currentAnimation: String,
    onPlayAnimation: (String) -> Unit,
    onCustomizeAvatar: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = OurWorldSoftWhite.copy(alpha = 0.95f),
        shadowElevation = 4.dp
    ) {
        LazyRow(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(AvatarAnimation.entries.toList()) { anim ->
                EmojiButton(
                    emoji = anim.emoji,
                    label = anim.label,
                    isSelected = currentAnimation == anim.name,
                    onClick = { onPlayAnimation(anim.name) },
                    size = 40
                )
            }
            item {
                EmojiButton(
                    emoji = "👤",
                    label = "Avatar",
                    onClick = onCustomizeAvatar,
                    size = 40
                )
            }
        }
    }
}

@Composable
private fun CustomizePanel(
    onUpdateWallpaper: (Int) -> Unit,
    onUpdateLighting: (Int) -> Unit,
    onAddFurniture: (String, String, String) -> Unit,
    currentWallpaper: Int,
    currentLighting: Int
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("🎨 Walls", "💡 Light", "🛋️ Add")

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = OurWorldSoftWhite,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = OurWorldRose,
                indicator = { }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) OurWorldRose else OurWorldWarmBrown
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (selectedTab) {
                0 -> {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(Wallpaper.entries.toList()) { wp ->
                            val index = wp.ordinal
                            val isSelected = currentWallpaper == index
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onUpdateWallpaper(index) }
                                    .padding(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color(wp.color))
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) OurWorldRose else OurWorldLightRose,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Text(
                                            "✓",
                                            color = if (wp == Wallpaper.STARRY) OurWorldSoftWhite else OurWorldRose,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    wp.label,
                                    fontSize = 11.sp,
                                    color = if (isSelected) OurWorldRose else OurWorldWarmBrown,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
                1 -> {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(Lighting.entries.toList()) { light ->
                            val index = light.ordinal
                            EmojiButton(
                                emoji = light.emoji,
                                label = light.label,
                                onClick = { onUpdateLighting(index) },
                                isSelected = currentLighting == index
                            )
                        }
                    }
                }
                2 -> {
                    val shopItems = listOf(
                        Triple("sofa", "Sofa", "🛋️"),
                        Triple("bed", "Bed", "🛏️"),
                        Triple("table", "Table", "🪑"),
                        Triple("lamp", "Lamp", "💡"),
                        Triple("bookshelf", "Bookshelf", "📚"),
                        Triple("tv", "TV", "📺"),
                        Triple("plant", "Plant", "🌿"),
                        Triple("candle", "Candles", "🕯️"),
                        Triple("frame", "Frame", "🖼️"),
                        Triple("rug", "Rug", "🟫"),
                        Triple("clock", "Clock", "🕐"),
                        Triple("guitar", "Guitar", "🎸")
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(shopItems) { (type, name, emoji) ->
                            EmojiButton(
                                emoji = emoji,
                                label = name,
                                onClick = { onAddFurniture(type, name, emoji) }
                            )
                        }
                    }
                }
            }
        }
    }
}
