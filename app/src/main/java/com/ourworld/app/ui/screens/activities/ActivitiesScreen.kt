package com.ourworld.app.ui.screens.activities

import androidx.compose.animation.AnimatedVisibility
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
import com.ourworld.app.ui.components.CoinDisplay
import com.ourworld.app.ui.components.CozyCard
import com.ourworld.app.ui.components.LevelBadge
import com.ourworld.app.ui.components.SectionHeader
import com.ourworld.app.ui.theme.*
import com.ourworld.app.ui.viewmodel.ActivitiesUiState
import kotlinx.coroutines.delay

@Composable
fun ActivitiesScreen(
    uiState: ActivitiesUiState,
    userCoins: Int,
    userLevel: Int,
    userXp: Int,
    onClaimReward: (CoupleChallenge) -> Unit,
    onClearRewardMessage: () -> Unit
) {
    LaunchedEffect(uiState.rewardMessage) {
        if (uiState.rewardMessage != null) {
            delay(2800)
            onClearRewardMessage()
        }
    }

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
                    .padding(top = 16.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Activities ✨",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = OurWorldDarkBrown
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    LevelBadge(level = userLevel, xp = userXp)
                    CoinDisplay(coins = userCoins)
                }
            }
        }

        item {
            AnimatedVisibility(visible = uiState.rewardMessage != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = OurWorldMint.copy(alpha = 0.85f)
                ) {
                    Text(
                        uiState.rewardMessage ?: "",
                        color = OurWorldDarkBrown,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        }

        item {
            SectionHeader(title = "Couple Challenges", emoji = "🏆")
        }

        items(uiState.challenges) { challenge ->
            ChallengeCard(
                challenge = challenge,
                isLoading = uiState.isLoading,
                onClaim = { onClaimReward(challenge) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun ChallengeCard(
    challenge: CoupleChallenge,
    isLoading: Boolean,
    onClaim: () -> Unit
) {
    val canClaim = challenge.progress >= 1f && !challenge.isCompleted

    CozyCard(modifier = Modifier.fillMaxWidth()) {
        Column {
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
                        color = if (challenge.isCompleted) OurWorldSuccess else OurWorldRose,
                        trackColor = OurWorldLightGray
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            if (challenge.isCompleted) "Claimed ✓" else "${(challenge.progress * 100).toInt()}%",
                            fontSize = 11.sp,
                            color = if (challenge.isCompleted) OurWorldSuccess else OurWorldWarmBrown,
                            fontWeight = if (challenge.isCompleted) FontWeight.SemiBold else FontWeight.Normal
                        )
                        Text(
                            "⭐ ${challenge.xpReward} XP  🪙 ${challenge.coinReward}",
                            fontSize = 11.sp,
                            color = OurWorldDarkBrown,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (canClaim) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onClaim,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OurWorldGold)
                ) {
                    Text(
                        "🎁 Claim Reward (+${challenge.coinReward} 🪙, +${challenge.xpReward} XP)",
                        color = OurWorldDarkBrown,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
