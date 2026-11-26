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
import com.ourworld.app.ui.components.CozyCard
import com.ourworld.app.ui.components.SectionHeader
import com.ourworld.app.ui.theme.*
import com.ourworld.app.ui.viewmodel.ActivitiesUiState

@Composable
fun ActivitiesScreen(
    uiState: ActivitiesUiState
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
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
        }

        item {
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
