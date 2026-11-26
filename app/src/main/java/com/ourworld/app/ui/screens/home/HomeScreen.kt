package com.ourworld.app.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import kotlin.math.roundToInt

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
    val isDarkWall = wallpaper == Wallpaper.STARRY
    val headerTextColor = if (isDarkWall) OurWorldSoftWhite else OurWorldDarkBrown
    val subHeaderColor = if (isDarkWall) OurWorldLightRose else OurWorldWarmBrown

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showUnpairConfirm by remember { mutableStateOf(false) }

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
                    Spacer(modifier = Modifier.width(4.dp))
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
                    color = OurWorldPeach.copy(alpha = 0.9f)
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

            // Room area with furniture and avatars
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                val roomWidthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)
                val roomHeightPx = constraints.maxHeight.toFloat().coerceAtLeast(1f)

                // Floor
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.35f)
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    OurWorldPeach.copy(alpha = 0.35f),
                                    OurWorldPeach.copy(alpha = 0.7f)
                                )
                            )
                        )
                )

                // Furniture items positioned proportionally to room size
                uiState.furniture.forEach { item ->
                    FurnitureOnCanvas(
                        item = item,
                        roomWidthPx = roomWidthPx,
                        roomHeightPx = roomHeightPx,
                        isEditing = uiState.isCustomizing,
                        onMove = { x, y -> onMoveFurniture(item.id, x, y) },
                        onRemove = { onRemoveFurniture(item.id) }
                    )
                }

                // Avatars
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                ) {
                    AvatarPairDisplay(
                        myAvatar = uiState.myAvatar,
                        partnerAvatar = uiState.partnerAvatar,
                        myName = uiState.myName,
                        partnerName = uiState.partnerName,
                        avatarSize = 90
                    )
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
private fun FurnitureOnCanvas(
    item: FurnitureItem,
    roomWidthPx: Float,
    roomHeightPx: Float,
    isEditing: Boolean,
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
                } else Modifier
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
        color = OurWorldSoftWhite.copy(alpha = 0.95f)
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
