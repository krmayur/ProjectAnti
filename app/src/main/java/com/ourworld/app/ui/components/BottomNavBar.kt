package com.ourworld.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourworld.app.ui.theme.*

data class BottomNavItem(
    val route: String,
    val label: String,
    val emoji: String
)

val bottomNavItems = listOf(
    BottomNavItem("home", "Home", "🏠"),
    BottomNavItem("garden", "Garden", "🌱"),
    BottomNavItem("activities", "Activities", "✨"),
    BottomNavItem("gifts", "Gifts", "🎁"),
    BottomNavItem("memories", "Memories", "📸")
)

@Composable
fun OurWorldBottomNav(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = OurWorldSoftWhite,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val isSelected = currentRoute == item.route
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.1f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "nav_scale"
                )
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) OurWorldPeach else Color.Transparent,
                    label = "nav_bg"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .scale(scale)
                        .clip(RoundedCornerShape(16.dp))
                        .background(bgColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onNavigate(item.route) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        item.emoji,
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        item.label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) OurWorldRose else OurWorldWarmBrown
                    )
                }
            }
        }
    }
}
