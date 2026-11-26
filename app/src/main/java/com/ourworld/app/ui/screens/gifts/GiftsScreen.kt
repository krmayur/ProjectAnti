package com.ourworld.app.ui.screens.gifts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourworld.app.data.model.Gift
import com.ourworld.app.data.model.GiftType
import com.ourworld.app.data.model.HandwrittenNote
import com.ourworld.app.ui.components.*
import com.ourworld.app.ui.theme.*
import com.ourworld.app.ui.viewmodel.GiftsUiState

@Composable
fun GiftsScreen(
    uiState: GiftsUiState,
    coupleId: String,
    userId: String,
    partnerId: String,
    onSendGift: (GiftType, String) -> Unit,
    onOpenGift: (String) -> Unit,
    onSendNote: (String) -> Unit,
    onToggleSendingGift: () -> Unit,
    onToggleWritingNote: () -> Unit
) {
    var selectedGiftType by remember { mutableStateOf<GiftType?>(null) }
    var giftMessage by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }
    var revealedGift by remember { mutableStateOf<Gift?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(OurWorldCream)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Gifts & Notes 🎁",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = OurWorldDarkBrown
                    )
                    Text(
                        "Today: ${uiState.gifts.size} gifts • ${uiState.notes.size} notes (resets daily 🌅)",
                        fontSize = 12.sp,
                        color = OurWorldWarmBrown
                    )
                }
                Row {
                    IconButton(onClick = onToggleWritingNote) {
                        Icon(Icons.Outlined.Edit, "Write Note", tint = OurWorldRose)
                    }
                    IconButton(onClick = onToggleSendingGift) {
                        Icon(Icons.Default.Add, "Send Gift", tint = OurWorldRose)
                    }
                }
            }
        }

        // Send gift panel
        item {
            AnimatedVisibility(visible = uiState.isSendingGift) {
                CozyCard(modifier = Modifier.fillMaxWidth()) {
                    Text("Send a Gift 🎁", fontWeight = FontWeight.Bold, color = OurWorldDarkBrown)
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(GiftType.entries.toList()) { type ->
                            EmojiButton(
                                emoji = type.emoji,
                                label = "${type.label}\n🪙${type.cost}",
                                onClick = { selectedGiftType = type },
                                isSelected = selectedGiftType == type
                            )
                        }
                    }
                    if (selectedGiftType != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OurWorldTextField(
                            value = giftMessage,
                            onValueChange = { giftMessage = it },
                            label = "Add a message 💌"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OurWorldButton(
                            text = "Send ${selectedGiftType?.emoji} 💕",
                            onClick = {
                                selectedGiftType?.let { onSendGift(it, giftMessage) }
                                giftMessage = ""
                                selectedGiftType = null
                            },
                            isLoading = uiState.isLoading
                        )
                    }
                }
            }
        }

        // Write note panel
        item {
            AnimatedVisibility(visible = uiState.isWritingNote) {
                CozyCard(modifier = Modifier.fillMaxWidth()) {
                    Text("Write a Note 💌", fontWeight = FontWeight.Bold, color = OurWorldDarkBrown)
                    Spacer(modifier = Modifier.height(12.dp))
                    OurWorldTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        label = "Your message...",
                        singleLine = false
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OurWorldButton(
                        text = "Send Note ✉️",
                        onClick = {
                            onSendNote(noteContent)
                            noteContent = ""
                        },
                        isLoading = uiState.isLoading,
                        enabled = noteContent.isNotBlank()
                    )
                }
            }
        }

        // Received gifts
        if (uiState.gifts.isNotEmpty()) {
            item { SectionHeader(title = "Gifts", emoji = "🎁") }
            items(uiState.gifts) { gift ->
                GiftCard(
                    gift = gift,
                    isMyGift = gift.toUserId == userId || gift.fromUserId != userId,
                    onOpen = {
                        onOpenGift(gift.id)
                        revealedGift = gift.copy(isOpened = true)
                    }
                )
            }
        }

        // Notes
        if (uiState.notes.isNotEmpty()) {
            item { SectionHeader(title = "Love Notes", emoji = "💌") }
            items(uiState.notes) { note ->
                NoteCard(note = note, isMyNote = note.fromUserId == userId)
            }
        }

        if (uiState.gifts.isEmpty() && uiState.notes.isEmpty()) {
            item {
                EmptyStateMessage(
                    emoji = "🎁",
                    title = "No gifts today yet!",
                    subtitle = "Send your first gift or note to your partner today 💕"
                )
            }
        }

        item { Spacer(modifier = Modifier.height(100.dp)) }
    }

    // Gift Reveal Dialog
    revealedGift?.let { gift ->
        AlertDialog(
            onDismissRequest = { revealedGift = null },
            containerColor = OurWorldSoftWhite,
            shape = RoundedCornerShape(24.dp),
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(gift.emoji, fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        gift.type.label,
                        fontWeight = FontWeight.Bold,
                        color = OurWorldDarkBrown
                    )
                }
            },
            text = {
                Text(
                    text = if (gift.message.isNotBlank()) "\"${gift.message}\"" else "A special surprise from your partner 💕",
                    color = OurWorldWarmBrown,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = { revealedGift = null }) {
                    Text("Love it 💕", color = OurWorldRose, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}

@Composable
private fun GiftCard(
    gift: Gift,
    isMyGift: Boolean,
    onOpen: () -> Unit
) {
    val scale by rememberInfiniteTransition(label = "gift").animateFloat(
        initialValue = 1f,
        targetValue = if (!gift.isOpened && isMyGift) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gift_bounce"
    )

    CozyCard(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        onClick = if (!gift.isOpened && isMyGift) onOpen else null
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                if (gift.isOpened || !isMyGift) gift.emoji else "🎁",
                fontSize = 36.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    if (gift.isOpened || !isMyGift) gift.type.label else "Mystery Gift",
                    fontWeight = FontWeight.SemiBold,
                    color = OurWorldDarkBrown
                )
                if (gift.message.isNotEmpty() && (gift.isOpened || !isMyGift)) {
                    Text(
                        "\"${gift.message}\"",
                        fontSize = 13.sp,
                        color = OurWorldWarmBrown,
                        fontWeight = FontWeight.Light
                    )
                }
                Text(
                    if (isMyGift) "From your partner 💕" else if (gift.isOpened) "Opened by your partner ✓" else "Sent by you • Waiting to open",
                    fontSize = 11.sp,
                    color = OurWorldLightRose
                )
            }
            if (!gift.isOpened && isMyGift) {
                Text("Tap to open!", fontSize = 11.sp, color = OurWorldRose, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun NoteCard(note: HandwrittenNote, isMyNote: Boolean) {
    CozyCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row {
                Text("💌", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (isMyNote) "You wrote:" else "From your partner:",
                    fontSize = 12.sp,
                    color = OurWorldWarmBrown
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = OurWorldPeach.copy(alpha = 0.4f)
            ) {
                Text(
                    note.content,
                    modifier = Modifier.padding(16.dp),
                    color = OurWorldDarkBrown,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            }
        }
    }
}
