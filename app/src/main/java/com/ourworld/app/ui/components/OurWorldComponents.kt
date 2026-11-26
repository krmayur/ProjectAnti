package com.ourworld.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourworld.app.ui.theme.*

@Composable
fun OurWorldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = OurWorldRose,
            contentColor = OurWorldSoftWhite,
            disabledContainerColor = OurWorldLightRose.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = OurWorldSoftWhite,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun OurWorldTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    leadingIcon: ImageVector? = null,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        shape = RoundedCornerShape(16.dp),
        leadingIcon = leadingIcon?.let { icon ->
            { Icon(icon, contentDescription = null, tint = OurWorldRose) }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = OurWorldRose,
            unfocusedBorderColor = OurWorldLightRose,
            focusedLabelColor = OurWorldRose,
            cursorColor = OurWorldRose,
            focusedContainerColor = OurWorldSoftWhite,
            unfocusedContainerColor = OurWorldSoftWhite
        ),
        visualTransformation = if (isPassword) {
            androidx.compose.ui.text.input.PasswordVisualTransformation()
        } else {
            androidx.compose.ui.text.input.VisualTransformation.None
        }
    )
}

@Composable
fun CozyCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .then(
                if (onClick != null) Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                ) else Modifier
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = OurWorldSoftWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun EmojiButton(
    emoji: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    size: Int = 48
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "emoji_scale"
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(
                    if (isSelected) OurWorldPeach else OurWorldLightGray
                )
                .then(
                    if (isSelected) Modifier.border(2.dp, OurWorldRose, CircleShape)
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = (size / 2).sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            fontSize = 11.sp,
            color = if (isSelected) OurWorldRose else OurWorldWarmBrown,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun FloatingHearts() {
    val hearts = remember { List(6) { it } }
    Box(modifier = Modifier.fillMaxSize()) {
        hearts.forEach { index ->
            val infiniteTransition = rememberInfiniteTransition(label = "heart_$index")
            val offsetY by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = -0.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 4000 + index * 800,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "heart_y_$index"
            )
            val offsetX by infiniteTransition.animateFloat(
                initialValue = 0.1f + index * 0.15f,
                targetValue = 0.15f + index * 0.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000 + index * 300, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "heart_x_$index"
            )
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.6f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(4000 + index * 800, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "heart_alpha_$index"
            )
            Text(
                text = if (index % 2 == 0) "💕" else "✨",
                fontSize = (14 + index * 2).sp,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.TopStart)
                    .offset(
                        x = (offsetX * 300).dp,
                        y = (offsetY * 800).dp
                    )
                    .alpha(alpha)
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    emoji: String = "",
    action: String = "",
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (emoji.isNotEmpty()) {
                Text(emoji, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        if (action.isNotEmpty() && onAction != null) {
            TextButton(onClick = onAction) {
                Text(action, color = OurWorldRose)
            }
        }
    }
}

@Composable
fun CoinDisplay(coins: Int, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = OurWorldGold.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🪙", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "$coins",
                fontWeight = FontWeight.Bold,
                color = OurWorldDarkBrown,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun LevelBadge(level: Int, xp: Int, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = OurWorldLavender.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⭐", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "Lv.$level",
                fontWeight = FontWeight.Bold,
                color = OurWorldDarkBrown,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun EmptyStateMessage(
    emoji: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 48.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = OurWorldWarmBrown
        )
    }
}
