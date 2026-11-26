package com.ourworld.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourworld.app.data.model.Avatar
import com.ourworld.app.data.model.AvatarAnimation
import com.ourworld.app.data.model.HairColor
import com.ourworld.app.data.model.HairStyle
import com.ourworld.app.data.model.SkinTone
import com.ourworld.app.ui.theme.*

val OutfitColors = listOf(
    "Rose" to 0xFFE57A90L,
    "Lavender" to 0xFFB8A4E8L,
    "Mint" to 0xFF7BC89FL,
    "Sky" to 0xFF81B9F5L,
    "Peach" to 0xFFF4A261L,
    "Cream" to 0xFFE8D5C4L
)

val AvatarAccessories = listOf(
    "None" to "",
    "Glasses" to "👓",
    "Bow" to "🎀",
    "Flower" to "🌸",
    "Headphones" to "🎧",
    "Crown" to "👑"
)

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
    val outfitColor = Color(OutfitColors.getOrElse(avatar.outfitTop) { OutfitColors[0] }.second)
    val accessoryEmoji = AvatarAccessories.getOrElse(avatar.accessory) { AvatarAccessories[0] }.second

    val infiniteTransition = rememberInfiniteTransition(label = "avatar_anim")
    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = when (animation) {
            AvatarAnimation.DANCE -> -10f
            AvatarAnimation.SIT -> 6f
            AvatarAnimation.IDLE -> -2f
            else -> -6f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (animation == AvatarAnimation.DANCE) 420 else 1100,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    val sway by infiniteTransition.animateFloat(
        initialValue = if (animation == AvatarAnimation.DANCE) -5f else 0f,
        targetValue = if (animation == AvatarAnimation.DANCE) 5f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(420, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sway"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .offset(y = bounce.dp)
                .rotate(sway),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = this.size.width
                val h = this.size.height
                val skin = Color(skinTone.color)
                val hair = Color(hairColor.color)

                // Back hair for long/ponytail/curly styles
                when (hairStyle) {
                    HairStyle.LONG -> {
                        drawRoundRect(
                            color = hair,
                            topLeft = Offset(w * 0.22f, h * 0.22f),
                            size = Size(w * 0.56f, h * 0.52f),
                            cornerRadius = CornerRadius(w * 0.2f, w * 0.2f)
                        )
                    }
                    HairStyle.CURLY -> {
                        drawCircle(hair, radius = w * 0.30f, center = Offset(w * 0.5f, h * 0.31f))
                        drawCircle(hair, radius = w * 0.12f, center = Offset(w * 0.25f, h * 0.38f))
                        drawCircle(hair, radius = w * 0.12f, center = Offset(w * 0.75f, h * 0.38f))
                    }
                    HairStyle.PONYTAIL -> {
                        drawOval(
                            color = hair,
                            topLeft = Offset(w * 0.66f, h * 0.24f),
                            size = Size(w * 0.20f, h * 0.34f)
                        )
                    }
                    HairStyle.BUN -> {
                        drawCircle(hair, radius = w * 0.12f, center = Offset(w * 0.5f, h * 0.09f))
                    }
                    HairStyle.BRAIDS -> {
                        drawRoundRect(
                            color = hair,
                            topLeft = Offset(w * 0.20f, h * 0.30f),
                            size = Size(w * 0.12f, h * 0.40f),
                            cornerRadius = CornerRadius(w * 0.06f, w * 0.06f)
                        )
                        drawRoundRect(
                            color = hair,
                            topLeft = Offset(w * 0.68f, h * 0.30f),
                            size = Size(w * 0.12f, h * 0.40f),
                            cornerRadius = CornerRadius(w * 0.06f, w * 0.06f)
                        )
                    }
                    else -> {}
                }

                // Soft shadow under avatar
                drawOval(
                    color = Color.Black.copy(alpha = 0.08f),
                    topLeft = Offset(w * 0.22f, h * 0.90f),
                    size = Size(w * 0.56f, h * 0.08f)
                )

                // Torso / Outfit
                val torsoHeight = if (animation == AvatarAnimation.SIT) h * 0.34f else h * 0.40f
                val torsoTop = if (animation == AvatarAnimation.SIT) h * 0.56f else h * 0.52f
                drawRoundRect(
                    color = outfitColor,
                    topLeft = Offset(w * 0.22f, torsoTop),
                    size = Size(w * 0.56f, torsoHeight),
                    cornerRadius = CornerRadius(w * 0.24f, w * 0.24f)
                )

                // Cute white collar / heart detail on shirt
                drawCircle(
                    color = Color.White.copy(alpha = 0.55f),
                    radius = w * 0.045f,
                    center = Offset(w * 0.5f, torsoTop + h * 0.13f)
                )

                // Arms
                drawRoundRect(
                    color = skin,
                    topLeft = Offset(w * 0.14f, torsoTop + h * 0.04f),
                    size = Size(w * 0.10f, h * 0.22f),
                    cornerRadius = CornerRadius(w * 0.05f, w * 0.05f)
                )
                drawRoundRect(
                    color = skin,
                    topLeft = Offset(w * 0.76f, torsoTop + h * 0.04f),
                    size = Size(w * 0.10f, h * 0.22f),
                    cornerRadius = CornerRadius(w * 0.05f, w * 0.05f)
                )

                // Neck
                drawRect(
                    color = skin,
                    topLeft = Offset(w * 0.43f, h * 0.45f),
                    size = Size(w * 0.14f, h * 0.10f)
                )

                // Head
                val headCenter = Offset(w * 0.5f, h * 0.31f)
                val headRadius = w * 0.24f
                drawCircle(color = skin, radius = headRadius, center = headCenter)

                // Front Hair / Bangs
                val hairPath = Path().apply {
                    moveTo(w * 0.26f, h * 0.30f)
                    cubicTo(
                        w * 0.26f, h * 0.08f,
                        w * 0.74f, h * 0.08f,
                        w * 0.74f, h * 0.30f
                    )
                    when (hairStyle) {
                        HairStyle.SHORT, HairStyle.PIXIE -> {
                            lineTo(w * 0.62f, h * 0.20f)
                            lineTo(w * 0.50f, h * 0.23f)
                            lineTo(w * 0.38f, h * 0.20f)
                        }
                        else -> {
                            quadraticBezierTo(w * 0.62f, h * 0.20f, w * 0.50f, h * 0.24f)
                            quadraticBezierTo(w * 0.38f, h * 0.20f, w * 0.26f, h * 0.30f)
                        }
                    }
                    close()
                }
                drawPath(hairPath, color = hair)

                // Rosy blush cheeks
                drawOval(
                    color = OurWorldRose.copy(alpha = 0.28f),
                    topLeft = Offset(w * 0.30f, h * 0.34f),
                    size = Size(w * 0.09f, h * 0.05f)
                )
                drawOval(
                    color = OurWorldRose.copy(alpha = 0.28f),
                    topLeft = Offset(w * 0.61f, h * 0.34f),
                    size = Size(w * 0.09f, h * 0.05f)
                )

                // Eyes
                if (avatar.expression == "sleepy") {
                    drawArc(
                        color = OurWorldDarkBrown,
                        startAngle = 0f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(w * 0.37f, h * 0.29f),
                        size = Size(w * 0.07f, h * 0.04f),
                        style = Stroke(width = w * 0.02f, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = OurWorldDarkBrown,
                        startAngle = 0f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(w * 0.56f, h * 0.29f),
                        size = Size(w * 0.07f, h * 0.04f),
                        style = Stroke(width = w * 0.02f, cap = StrokeCap.Round)
                    )
                } else {
                    drawCircle(OurWorldDarkBrown, radius = w * 0.032f, center = Offset(w * 0.41f, h * 0.31f))
                    drawCircle(OurWorldDarkBrown, radius = w * 0.032f, center = Offset(w * 0.59f, h * 0.31f))
                    // Eye sparkles
                    drawCircle(Color.White, radius = w * 0.011f, center = Offset(w * 0.40f, h * 0.30f))
                    drawCircle(Color.White, radius = w * 0.011f, center = Offset(w * 0.58f, h * 0.30f))
                }

                // Smile
                drawArc(
                    color = OurWorldDarkRose,
                    startAngle = 15f,
                    sweepAngle = 150f,
                    useCenter = false,
                    topLeft = Offset(w * 0.44f, h * 0.34f),
                    size = Size(w * 0.12f, h * 0.06f),
                    style = Stroke(width = w * 0.022f, cap = StrokeCap.Round)
                )
            }

            // Accessory overlay
            if (accessoryEmoji.isNotEmpty()) {
                Text(
                    text = accessoryEmoji,
                    fontSize = (size * 0.22f).sp,
                    modifier = Modifier
                        .align(
                            if (avatar.accessory == 1) Alignment.Center else Alignment.TopCenter
                        )
                        .offset(
                            y = if (avatar.accessory == 1) -(size * 0.18f).dp else -(size * 0.06f).dp
                        )
                )
            }

            // Animation badge
            if (animation != AvatarAnimation.IDLE) {
                Text(
                    text = animation.emoji,
                    fontSize = (size * 0.22f).sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (size * 0.06f).dp, y = -(size * 0.02f).dp)
                )
            }
        }

        if (showName) {
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = OurWorldSoftWhite.copy(alpha = 0.85f)
            ) {
                Text(
                    text = name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OurWorldDarkBrown,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                )
            }
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
    avatarSize: Int = 100,
    onHeartClick: (() -> Unit)? = null,
    onMyAvatarClick: (() -> Unit)? = null
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
            isAnimating = myAvatar.currentAnimation != "idle",
            onClick = onMyAvatarClick
        )
        val heartBeat by rememberInfiniteTransition(label = "heartbeat").animateFloat(
            initialValue = 1f,
            targetValue = 1.25f,
            animationSpec = infiniteRepeatable(
                animation = tween(850, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "heartbeat_scale"
        )
        Text(
            text = "💕",
            fontSize = 26.sp,
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .scale(heartBeat)
                .offset(y = (-28).dp)
                .then(if (onHeartClick != null) Modifier.clickable(onClick = onHeartClick) else Modifier)
        )
        if (partnerAvatar != null) {
            AvatarDisplay(
                avatar = partnerAvatar,
                name = partnerName,
                size = avatarSize,
                isAnimating = partnerAvatar.currentAnimation != "idle"
            )
        } else {
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
