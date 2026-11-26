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
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourworld.app.data.model.Achievement
import com.ourworld.app.data.model.MemoryType
import com.ourworld.app.data.model.Milestone
import com.ourworld.app.ui.components.*
import com.ourworld.app.ui.theme.*
import com.ourworld.app.ui.viewmodel.MemoriesUiState
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoriesScreen(
    uiState: MemoriesUiState,
    coupleId: String,
    userId: String,
    onAddMemory: (String, String, MemoryType) -> Unit,
    onSetAnniversaryDate: (Long) -> Unit,
    onToggleAdding: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(MemoryType.MOMENT) }
    var showDatePicker by remember { mutableStateOf(false) }

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

        // Anniversary & Together Counter Card
        item {
            AnniversaryCounterCard(
                anniversaryMillis = uiState.anniversaryDate,
                onPickDate = { showDatePicker = true }
            )
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

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.anniversaryDate ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selected ->
                            onSetAnniversaryDate(selected)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Save 💕", color = OurWorldRose, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = OurWorldWarmBrown)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun AnniversaryCounterCard(
    anniversaryMillis: Long?,
    onPickDate: () -> Unit
) {
    val startDate = anniversaryMillis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
    }
    val today = LocalDate.now()
    val effectiveStart = if (startDate != null && !startDate.isAfter(today)) startDate else today
    val period = Period.between(effectiveStart, today)
    val totalDays = ChronoUnit.DAYS.between(effectiveStart, today).coerceAtLeast(0L)

    CozyCard(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Together For 💕",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OurWorldDarkBrown
                    )
                    Text(
                        if (anniversaryMillis != null) "Since ${formatDate(anniversaryMillis)}"
                        else "Tap calendar to set your anniversary",
                        fontSize = 12.sp,
                        color = OurWorldWarmBrown
                    )
                }
                OutlinedButton(
                    onClick = onPickDate,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = OurWorldRose)
                ) {
                    Icon(
                        Icons.Outlined.CalendarMonth,
                        contentDescription = "Set Anniversary",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (anniversaryMillis != null) "Edit" else "Set Date", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TimeUnitBox(
                    value = period.years,
                    label = if (period.years == 1) "Year" else "Years",
                    bgColor = OurWorldPeach.copy(alpha = 0.7f),
                    modifier = Modifier.weight(1f)
                )
                TimeUnitBox(
                    value = period.months,
                    label = if (period.months == 1) "Month" else "Months",
                    bgColor = OurWorldLavender.copy(alpha = 0.55f),
                    modifier = Modifier.weight(1f)
                )
                TimeUnitBox(
                    value = period.days,
                    label = if (period.days == 1) "Day" else "Days",
                    bgColor = OurWorldMint.copy(alpha = 0.65f),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                "$totalDays days together",
                fontSize = 12.sp,
                color = OurWorldRose,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TimeUnitBox(
    value: Int,
    label: String,
    bgColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = bgColor
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$value",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = OurWorldDarkBrown
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = OurWorldWarmBrown,
                fontWeight = FontWeight.Medium
            )
        }
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
