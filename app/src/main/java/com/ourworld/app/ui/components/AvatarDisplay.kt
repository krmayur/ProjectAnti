package com.ourworld.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourworld.app.data.model.Avatar
import com.ourworld.app.data.model.AvatarAnimation
import com.ourworld.app.data.model.HairColor
import com.ourworld.app.data.model.HairStyle
import com.ourworld.app.data.model.SkinTone
import com.ourworld.app.ui.theme.*

@Composable
fun AvatarDisplay(
    avatar: Avatar,
    name: String,
    modifier: Modifier = Modifier,
    size: Int = 120,
    showName: Boolean = true,
    isAnimating: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val skinTone = SkinTone.entries.getOrElse(avatar.skinTone) { SkinTone.LIGHT }
    val hairStyle = HairStyle.entries.getOrElse(avatar.hairStyle) { HairStyle.MEDIUM }
    val hairColor = HairColor.entries.getOrElse(avatar.hairColor) { HairColor.BROWN }
    val animation = AvatarAnimation.entries.find { it.name == avatar.currentAnimation } ?: AvatarAnimation.IDLE

    val infiniteTransition = rememberInfiniteTransition(label = "avatar_anim")
    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isAnimating) -8f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .offset(y = bounce.dp),
            contentAlignment = Alignment.Center
        ) {
            // Body circle (skin)
            Box(
                modifier = Modifier
                    .size((size * 0.7f).dp)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(topStart = (size * 0.35f).dp, topEnd = (size * 0.35f).dp, bottomStart = (size * 0.15f).dp, bottomEnd = (size * 0.15f).dp))
                    .background(Color(skinTone.color))
            )
            // Head circle
            Box(
                modifier = Modifier
                    .size((size * 0.5f).dp)
                    .align(Alignment.TopCenter)
                    .offset(y = (size * 0.05f).dp)
                    .clip(CircleShape)
                    .background(Color(skinTone.color))
            ) {
                // Hair on top of head
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(
                            when (hairStyle) {
                                HairStyle.SHORT -> 0.35f
                                HairStyle.PIXIE -> 0.4f
                                else -> 0.45f
                            }
                        )
                        .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                        .background(Color(hairColor.color))
                        .align(Alignment.TopCenter)
                )
                // Eyes
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = (size * 0.02f).dp)
                        .padding(horizontal = (size * 0.08f).dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left eye
                    Box(
                        modifier = Modifier
                            .size((size * 0.06f).dp)
                            .clip(CircleShape)
                            .background(OurWorldDarkBrown)
                    )
                    Spacer(modifier = Modifier.width((size * 0.1f).dp))
                    // Right eye
                    Box(
                        modifier = Modifier
                            .size((size * 0.06f).dp)
                            .clip(CircleShape)
                            .background(OurWorldDarkBrown)
                    )
                }
                // Mouth / expression
                Text(
                    text = when (avatar.expression) {
                        "happy" -> "◡"
                        "love" -> "♡"
                        "sleepy" -> "~"
                        else -> "◡"
                    },
                    fontSize = (size * 0.08f).sp,
                    color = OurWorldRose,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = (size * 0.1f).dp)
                )
            }
            // Animation emoji
            if (animation != AvatarAnimation.IDLE) {
                Text(
                    text = animation.emoji,
                    fontSize = (size * 0.2f).sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (size * 0.1f).dp, y = -(size * 0.05f).dp)
                )
            }
        }
        if (showName) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = OurWorldDarkBrown
            )
        }
    }
}

@Composable
fun AvatarPairDisplay(
    myAvatar: Avatar,
    partnerAvatar: Avatar?,
    myName: String,
    partnerName: String,
    modifier: Modifier = Modifier,
    avatarSize: Int = 100
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        AvatarDisplay(
            avatar = myAvatar,
            name = myName,
            size = avatarSize,
            isAnimating = myAvatar.currentAnimation != "idle"
        )
        // Heart between avatars
        val heartBeat by rememberInfiniteTransition(label = "heartbeat").animateFloat(
            initialValue = 1f,
            targetValue = 1.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "heartbeat_scale"
        )
        Text(
            text = "💕",
            fontSize = 24.sp,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .scale(heartBeat)
                .offset(y = (-20).dp)
        )
        if (partnerAvatar != null) {
            AvatarDisplay(
                avatar = partnerAvatar,
                name = partnerName,
                size = avatarSize,
                isAnimating = partnerAvatar.currentAnimation != "idle"
            )
        } else {
            // Placeholder for partner
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(avatarSize.dp)
                        .clip(CircleShape)
                        .background(OurWorldLightGray)
                        .border(2.dp, OurWorldLightRose, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("?", fontSize = (avatarSize * 0.3f).sp, color = OurWorldWarmBrown)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Waiting...", fontSize = 13.sp, color = OurWorldWarmBrown)
            }
        }
    }
}
