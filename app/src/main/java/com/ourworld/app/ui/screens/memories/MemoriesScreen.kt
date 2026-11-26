package com.ourworld.app.ui.screens.memories

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourworld.app.data.model.Achievement
import com.ourworld.app.data.model.MemoryType
import com.ourworld.app.data.model.Milestone
import com.ourworld.app.ui.components.*
import com.ourworld.app.ui.theme.*
import com.ourworld.app.ui.viewmodel.MemoriesUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MemoriesScreen(
    uiState: MemoriesUiState,
    coupleId: String,
    userId: String,
    onAddMemory: (String, String, MemoryType) -> Unit,
    onToggleAdding: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(MemoryType.MOMENT) }

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
                Text(
                    "Memories 📸",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = OurWorldDarkBrown
                )
                IconButton(onClick = onToggleAdding) {
                    Icon(Icons.Default.Add, "Add Memory", tint = OurWorldRose)
                }
            }
        }

        // Add memory panel
        item {
            AnimatedVisibility(visible = uiState.isAddingMemory) {
                CozyCard(modifier = Modifier.fillMaxWidth()) {
                    Text("New Memory ✨", fontWeight = FontWeight.Bold, color = OurWorldDarkBrown)
                    Spacer(modifier = Modifier.height(12.dp))
                    OurWorldTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = "Title"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OurWorldTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = "Description",
                        singleLine = false
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(MemoryType.entries.toList()) { type ->
                            EmojiButton(
                                emoji = type.emoji,
                                label = type.label,
                                onClick = { selectedType = type },
                                isSelected = selectedType == type,
                                size = 40
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OurWorldButton(
                        text = "Save Memory 💕",
                        onClick = {
                            onAddMemory(title, description, selectedType)
                            title = ""
                            description = ""
                        },
                        isLoading = uiState.isLoading,
                        enabled = title.isNotBlank()
                    )
                }
            }
        }

        // Milestones
        if (uiState.milestones.isNotEmpty()) {
            item { SectionHeader(title = "Milestones", emoji = "💕") }
            items(uiState.milestones) { milestone ->
                MilestoneCard(milestone)
            }
        }

        // Achievements
        if (uiState.achievements.isNotEmpty()) {
            item { SectionHeader(title = "Achievements", emoji = "🏆") }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.achievements) { achievement ->
                        AchievementBadge(achievement)
                    }
                }
            }
        }

        // Memories timeline
        if (uiState.memories.isNotEmpty()) {
            item { SectionHeader(title = "Timeline", emoji = "📖") }
            items(uiState.memories) { memory ->
                CozyCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(memory.emoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                memory.title,
                                fontWeight = FontWeight.SemiBold,
                                color = OurWorldDarkBrown
                            )
                            if (memory.description.isNotEmpty()) {
                                Text(
                                    memory.description,
                                    fontSize = 13.sp,
                                    color = OurWorldWarmBrown
                                )
                            }
                            Text(
                                formatDate(memory.date),
                                fontSize = 11.sp,
                                color = OurWorldLightRose
                            )
                        }
                    }
                }
            }
        }

        if (uiState.memories.isEmpty() && uiState.milestones.isEmpty()) {
            item {
                EmptyStateMessage(
                    emoji = "📸",
                    title = "No memories yet",
                    subtitle = "Start creating beautiful memories together! 💕"
                )
            }
        }

        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

@Composable
private fun MilestoneCard(milestone: Milestone) {
    CozyCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(milestone.emoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    milestone.title,
                    fontWeight = FontWeight.SemiBold,
                    color = OurWorldDarkBrown
                )
                Text(
                    milestone.description,
                    fontSize = 13.sp,
                    color = OurWorldWarmBrown
                )
            }
            if (milestone.isReached) {
                Text("✅", fontSize = 20.sp)
            } else {
                Text(
                    "${milestone.daysTogetherRequired}d",
                    fontSize = 12.sp,
                    color = OurWorldLightRose
                )
            }
        }
    }
}

@Composable
private fun AchievementBadge(achievement: Achievement) {
    CozyCard {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(100.dp)
        ) {
            Text(
                achievement.emoji,
                fontSize = 32.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                achievement.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (achievement.isUnlocked) OurWorldDarkBrown else OurWorldWarmBrown,
                maxLines = 2,
                lineHeight = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { if (achievement.requirement > 0) achievement.current.toFloat() / achievement.requirement else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = if (achievement.isUnlocked) OurWorldSuccess else OurWorldRose,
                trackColor = OurWorldLightGray
            )
            Text(
                "${achievement.current}/${achievement.requirement}",
                fontSize = 10.sp,
                color = OurWorldWarmBrown
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    return try {
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
    } catch (e: Exception) {
        ""
    }
}
