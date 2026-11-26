package com.ourworld.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourworld.app.data.model.*
import com.ourworld.app.ui.components.AvatarDisplay
import com.ourworld.app.ui.components.CozyCard
import com.ourworld.app.ui.components.EmojiButton
import com.ourworld.app.ui.components.SectionHeader
import com.ourworld.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarCustomizeScreen(
    currentAvatar: Avatar,
    userName: String,
    onSaveAvatar: (Avatar) -> Unit,
    onBack: () -> Unit
) {
    var avatar by remember(currentAvatar) { mutableStateOf(currentAvatar) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customize Avatar ✨", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onSaveAvatar(avatar); onBack() }) {
                        Icon(Icons.Default.Check, "Save", tint = OurWorldRose)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = OurWorldCream
                )
            )
        },
        containerColor = OurWorldCream
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Live preview
            CozyCard(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AvatarDisplay(
                        avatar = avatar,
                        name = userName,
                        size = 160,
                        isAnimating = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Skin Tone
            SectionHeader(title = "Skin Tone", emoji = "👤")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(SkinTone.entries.toList()) { tone ->
                    EmojiButton(
                        emoji = "👤",
                        label = tone.label,
                        onClick = { avatar = avatar.copy(skinTone = tone.ordinal) },
                        isSelected = avatar.skinTone == tone.ordinal
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hair Style
            SectionHeader(title = "Hair Style", emoji = "💇")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(HairStyle.entries.toList()) { style ->
                    EmojiButton(
                        emoji = when(style) {
                            HairStyle.SHORT -> "✂️"
                            HairStyle.MEDIUM -> "💇"
                            HairStyle.LONG -> "👩"
                            HairStyle.CURLY -> "🌀"
                            HairStyle.PONYTAIL -> "🎀"
                            HairStyle.BUN -> "🍡"
                            HairStyle.BRAIDS -> "🧶"
                            HairStyle.PIXIE -> "⚡"
                        },
                        label = style.label,
                        onClick = { avatar = avatar.copy(hairStyle = style.ordinal) },
                        isSelected = avatar.hairStyle == style.ordinal
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hair Color
            SectionHeader(title = "Hair Color", emoji = "🎨")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(HairColor.entries.toList()) { color ->
                    EmojiButton(
                        emoji = "🎨",
                        label = color.label,
                        onClick = { avatar = avatar.copy(hairColor = color.ordinal) },
                        isSelected = avatar.hairColor == color.ordinal
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Expression
            SectionHeader(title = "Expression", emoji = "😊")
            val expressions = listOf("happy" to "😊", "love" to "😍", "sleepy" to "😴")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(expressions) { (expr, emoji) ->
                    EmojiButton(
                        emoji = emoji,
                        label = expr.replaceFirstChar { it.uppercase() },
                        onClick = { avatar = avatar.copy(expression = expr) },
                        isSelected = avatar.expression == expr
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save button
            Button(
                onClick = { onSaveAvatar(avatar); onBack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OurWorldRose)
            ) {
                Text("Save Changes 💕", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
