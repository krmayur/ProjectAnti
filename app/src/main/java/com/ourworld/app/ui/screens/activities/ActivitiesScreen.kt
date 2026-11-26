package com.ourworld.app.ui.screens.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourworld.app.data.model.CoupleChallenge
import com.ourworld.app.data.model.DailyActivity
import com.ourworld.app.ui.components.CozyCard
import com.ourworld.app.ui.components.SectionHeader
import com.ourworld.app.ui.theme.*
import com.ourworld.app.ui.viewmodel.ActivitiesUiState

@Composable
fun ActivitiesScreen(
    uiState: ActivitiesUiState,
    onCompleteActivity: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(OurWorldCream)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "Activities ✨",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = OurWorldDarkBrown,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }

        item {
            SectionHeader(title = "Daily Activities", emoji = "📋")
        }

        items(uiState.dailyActivities) { activity ->
            DailyActivityCard(
                activity = activity,
                onComplete = { onCompleteActivity(activity.id) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(title = "Couple Challenges", emoji = "🏆")
        }

        items(uiState.challenges) { challenge ->
            ChallengeCard(challenge = challenge)
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun DailyActivityCard(
    activity: DailyActivity,
    onComplete: () -> Unit
) {
    CozyCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(activity.emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    activity.title,
                    fontWeight = FontWeight.SemiBold,
                    color = OurWorldDarkBrown
                )
                Text(
                    activity.description,
                    fontSize = 13.sp,
                    color = OurWorldWarmBrown
                )
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("⭐ ${activity.xpReward} XP", fontSize = 11.sp, color = OurWorldGold)
                    Text("🪙 ${activity.coinReward}", fontSize = 11.sp, color = OurWorldGold)
                }
            }
            if (!activity.isCompleted) {
                Button(
                    onClick = onComplete,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OurWorldMint),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Do ✓", color = OurWorldDarkBrown, fontSize = 13.sp)
                }
            } else {
                Text("✅", fontSize = 24.sp)
            }
        }
    }
}

@Composable
private fun ChallengeCard(challenge: CoupleChallenge) {
    CozyCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(challenge.emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    challenge.title,
                    fontWeight = FontWeight.SemiBold,
                    color = OurWorldDarkBrown
                )
                Text(
                    challenge.description,
                    fontSize = 13.sp,
                    color = OurWorldWarmBrown
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Progress bar
                LinearProgressIndicator(
                    progress = { challenge.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = OurWorldRose,
                    trackColor = OurWorldLightGray
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${(challenge.progress * 100).toInt()}%",
                        fontSize = 11.sp,
                        color = OurWorldWarmBrown
                    )
                    Text(
                        "⭐ ${challenge.xpReward} XP  🪙 ${challenge.coinReward}",
                        fontSize = 11.sp,
                        color = OurWorldGold
                    )
                }
            }
        }
    }
}
