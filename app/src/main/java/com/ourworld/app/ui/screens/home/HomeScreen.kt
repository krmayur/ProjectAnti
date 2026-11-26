package com.ourworld.app.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
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
    onNavigateToAvatar: () -> Unit
) {
    val wallpaper = Wallpaper.entries.getOrElse(uiState.home?.wallpaper ?: 0) { Wallpaper.CREAM }
    val lighting = Lighting.entries.getOrElse(uiState.home?.lighting ?: 0) { Lighting.WARM }

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
                Column {
                    Text(
                        "Our Home 🏠",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = OurWorldDarkBrown
                    )
                    Text(
                        "${lighting.emoji} ${lighting.label}",
                        fontSize = 12.sp,
                        color = OurWorldWarmBrown
                    )
                }
                Row {
                    CoinDisplay(coins = userCoins)
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onToggleCustomize) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Customize",
                            tint = OurWorldRose
                        )
                    }
                }
            }

            // Room area with furniture and avatars
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
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
                                    OurWorldPeach.copy(alpha = 0.3f),
                                    OurWorldPeach.copy(alpha = 0.6f)
                                )
                            )
                        )
                )

                // Furniture items
                uiState.furniture.forEach { item ->
                    FurnitureOnCanvas(
                        item = item,
                        isEditing = uiState.isCustomizing,
                        onMove = { x, y -> onMoveFurniture(item.id, x, y) },
                        onRemove = { onRemoveFurniture(item.id) }
                    )
                }

                // Avatars
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
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
}

@Composable
private fun FurnitureOnCanvas(
    item: FurnitureItem,
    isEditing: Boolean,
    onMove: (Float, Float) -> Unit,
    onRemove: () -> Unit
) {
    var offset by remember(item.id) { mutableStateOf(Offset(item.positionX * 300f, item.positionY * 500f)) }

    Box(
        modifier = Modifier
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .then(
                if (isEditing) {
                    Modifier.pointerInput(item.id) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offset = Offset(
                                (offset.x + dragAmount.x).coerceIn(0f, 300f),
                                (offset.y + dragAmount.y).coerceIn(0f, 500f)
                            )
                            onMove(offset.x / 300f, offset.y / 500f)
                        }
                    }
                } else Modifier
            )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(item.emoji, fontSize = 36.sp)
            if (isEditing) {
                Text(
                    "✕",
                    fontSize = 12.sp,
                    color = OurWorldError,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(OurWorldSoftWhite)
                        .padding(2.dp)
                        .clickable(onClick = onRemove)
                )
            }
        }
    }
}

@Composable
private fun AnimationBar(
    onPlayAnimation: (String) -> Unit,
    onCustomizeAvatar: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = OurWorldSoftWhite.copy(alpha = 0.9f)
    ) {
        LazyRow(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(AvatarAnimation.entries.toList()) { anim ->
                EmojiButton(
                    emoji = anim.emoji,
                    label = anim.label,
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
            // Tab row
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
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(Wallpaper.entries.toList()) { wp ->
                            val index = wp.ordinal
                            EmojiButton(
                                emoji = "",
                                label = wp.label,
                                onClick = { onUpdateWallpaper(index) },
                                isSelected = currentWallpaper == index
                            )
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
