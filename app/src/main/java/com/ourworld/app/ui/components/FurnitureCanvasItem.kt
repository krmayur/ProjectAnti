package com.ourworld.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourworld.app.data.model.FurnitureItem
import com.ourworld.app.ui.theme.*

fun furnitureDimensions(type: String): Pair<Dp, Dp> = when (type) {
    "sofa" -> 142.dp to 76.dp
    "bed" -> 158.dp to 92.dp
    "table" -> 86.dp to 48.dp
    "lamp" -> 54.dp to 124.dp
    "bookshelf" -> 78.dp to 116.dp
    "tv" -> 106.dp to 80.dp
    "plant" -> 58.dp to 86.dp
    "candle" -> 42.dp to 52.dp
    "frame" -> 68.dp to 56.dp
    "rug" -> 154.dp to 54.dp
    "clock" -> 48.dp to 48.dp
    "guitar" -> 44.dp to 92.dp
    else -> 56.dp to 56.dp
}

/**
 * Renders 2D Canvas-illustrated furniture & lighting with realistic relative sizes
 * and warm radial light halos for lamps and candles.
 */
@Composable
fun FurnitureIllustration(
    item: FurnitureItem,
    isWarmLighting: Boolean,
    modifier: Modifier = Modifier
) {
    val (widthDp, heightDp) = furnitureDimensions(item.type)
    val glowPulse by rememberInfiniteTransition(label = "glow_${item.id}").animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    val knownTypes = setOf(
        "sofa", "bed", "table", "lamp", "bookshelf",
        "tv", "plant", "candle", "frame", "rug", "clock", "guitar"
    )
    if (item.type !in knownTypes) {
        Text(item.emoji, fontSize = 38.sp, modifier = modifier)
        return
    }

    Canvas(modifier = modifier.size(widthDp, heightDp)) {
        val w = size.width
        val h = size.height

        when (item.type) {
            "sofa" -> {
                // Soft floor shadow
                drawOval(
                    Color.Black.copy(alpha = 0.10f),
                    Offset(w * 0.06f, h * 0.86f),
                    Size(w * 0.88f, h * 0.12f)
                )
                // Wooden legs
                drawRoundRect(Color(0xFF9C7A5B), Offset(w * 0.12f, h * 0.78f), Size(w * 0.06f, h * 0.14f), CornerRadius(4f, 4f))
                drawRoundRect(Color(0xFF9C7A5B), Offset(w * 0.82f, h * 0.78f), Size(w * 0.06f, h * 0.14f), CornerRadius(4f, 4f))
                // Backrest
                drawRoundRect(Color(0xFFE5989B), Offset(w * 0.08f, h * 0.12f), Size(w * 0.84f, h * 0.48f), CornerRadius(18f, 18f))
                // Seat cushions
                drawRoundRect(Color(0xFFF2B5B8), Offset(w * 0.10f, h * 0.44f), Size(w * 0.39f, h * 0.30f), CornerRadius(12f, 12f))
                drawRoundRect(Color(0xFFF2B5B8), Offset(w * 0.51f, h * 0.44f), Size(w * 0.39f, h * 0.30f), CornerRadius(12f, 12f))
                // Base
                drawRoundRect(Color(0xFFD67D84), Offset(w * 0.06f, h * 0.66f), Size(w * 0.88f, h * 0.16f), CornerRadius(10f, 10f))
                // Armrests
                drawRoundRect(Color(0xFFD67D84), Offset(0f, h * 0.34f), Size(w * 0.14f, h * 0.46f), CornerRadius(14f, 14f))
                drawRoundRect(Color(0xFFD67D84), Offset(w * 0.86f, h * 0.34f), Size(w * 0.14f, h * 0.46f), CornerRadius(14f, 14f))
                // Throw pillow
                drawRoundRect(OurWorldCream, Offset(w * 0.16f, h * 0.28f), Size(w * 0.16f, h * 0.22f), CornerRadius(8f, 8f))
            }

            "bed" -> {
                drawOval(Color.Black.copy(alpha = 0.10f), Offset(w * 0.05f, h * 0.86f), Size(w * 0.90f, h * 0.12f))
                // Headboard
                drawRoundRect(Color(0xFFB08968), Offset(w * 0.06f, h * 0.06f), Size(w * 0.88f, h * 0.42f), CornerRadius(16f, 16f))
                // Mattress
                drawRoundRect(OurWorldSoftWhite, Offset(w * 0.08f, h * 0.32f), Size(w * 0.84f, h * 0.48f), CornerRadius(12f, 12f))
                // Pillows
                drawRoundRect(Color(0xFFFFE5EC), Offset(w * 0.14f, h * 0.24f), Size(w * 0.32f, h * 0.18f), CornerRadius(10f, 10f))
                drawRoundRect(Color(0xFFFFE5EC), Offset(w * 0.54f, h * 0.24f), Size(w * 0.32f, h * 0.18f), CornerRadius(10f, 10f))
                // Cozy pastel blanket
                drawRoundRect(OurWorldLavender, Offset(w * 0.08f, h * 0.46f), Size(w * 0.84f, h * 0.34f), CornerRadius(12f, 12f))
                // Footboard
                drawRoundRect(Color(0xFF9C7A5B), Offset(w * 0.05f, h * 0.72f), Size(w * 0.90f, h * 0.14f), CornerRadius(8f, 8f))
            }

            "table" -> {
                drawOval(Color.Black.copy(alpha = 0.10f), Offset(w * 0.08f, h * 0.84f), Size(w * 0.84f, h * 0.12f))
                // Legs
                drawRoundRect(Color(0xFF8B6F5C), Offset(w * 0.16f, h * 0.42f), Size(w * 0.08f, h * 0.46f), CornerRadius(4f, 4f))
                drawRoundRect(Color(0xFF8B6F5C), Offset(w * 0.76f, h * 0.42f), Size(w * 0.08f, h * 0.46f), CornerRadius(4f, 4f))
                // Tabletop
                drawRoundRect(Color(0xFFC69C72), Offset(w * 0.04f, h * 0.30f), Size(w * 0.92f, h * 0.20f), CornerRadius(10f, 10f))
                // Cute coffee mug on table
                drawRoundRect(OurWorldRose, Offset(w * 0.56f, h * 0.12f), Size(w * 0.14f, h * 0.20f), CornerRadius(4f, 4f))
            }

            "lamp" -> {
                val haloAlpha = (if (isWarmLighting) 0.42f else 0.22f) * glowPulse
                // Warm radial light halo
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFE082).copy(alpha = haloAlpha), Color.Transparent),
                        center = Offset(w * 0.5f, h * 0.22f),
                        radius = w * 1.15f
                    ),
                    radius = w * 1.15f,
                    center = Offset(w * 0.5f, h * 0.22f)
                )
                // Base
                drawOval(Color.Black.copy(alpha = 0.10f), Offset(w * 0.22f, h * 0.90f), Size(w * 0.56f, h * 0.08f))
                drawRoundRect(Color(0xFF9C7A5B), Offset(w * 0.28f, h * 0.86f), Size(w * 0.44f, h * 0.08f), CornerRadius(6f, 6f))
                // Stand pole
                drawLine(Color(0xFFB08968), Offset(w * 0.5f, h * 0.32f), Offset(w * 0.5f, h * 0.88f), strokeWidth = 6f, cap = StrokeCap.Round)
                // Lampshade trapezoid
                val shade = Path().apply {
                    moveTo(w * 0.24f, h * 0.08f)
                    lineTo(w * 0.76f, h * 0.08f)
                    lineTo(w * 0.90f, h * 0.34f)
                    lineTo(w * 0.10f, h * 0.34f)
                    close()
                }
                drawPath(shade, color = Color(0xFFFFE5B4))
                drawPath(shade, color = Color(0xFFE6C288), style = Stroke(width = 3f))
            }

            "bookshelf" -> {
                drawOval(Color.Black.copy(alpha = 0.10f), Offset(w * 0.08f, h * 0.92f), Size(w * 0.84f, h * 0.06f))
                drawRoundRect(Color(0xFFB08968), Offset(w * 0.08f, h * 0.04f), Size(w * 0.84f, h * 0.90f), CornerRadius(10f, 10f))
                // Shelf interiors
                for (row in 0..2) {
                    val topY = h * (0.10f + row * 0.27f)
                    drawRoundRect(Color(0xFF7F5539), Offset(w * 0.14f, topY), Size(w * 0.72f, h * 0.22f), CornerRadius(4f, 4f))
                    // Books
                    val bookColors = listOf(OurWorldRose, OurWorldMint, OurWorldSky, OurWorldGold, OurWorldLavender)
                    for (b in 0..4) {
                        val bx = w * (0.17f + b * 0.13f)
                        val bh = h * (0.14f + ((row + b) % 2) * 0.04f)
                        drawRoundRect(
                            bookColors[(row + b) % bookColors.size],
                            Offset(bx, topY + h * 0.21f - bh),
                            Size(w * 0.10f, bh),
                            CornerRadius(3f, 3f)
                        )
                    }
                }
            }

            "tv" -> {
                drawOval(Color.Black.copy(alpha = 0.10f), Offset(w * 0.06f, h * 0.90f), Size(w * 0.88f, h * 0.08f))
                // Console
                drawRoundRect(Color(0xFFC69C72), Offset(w * 0.06f, h * 0.62f), Size(w * 0.88f, h * 0.28f), CornerRadius(10f, 10f))
                drawRect(Color(0xFF9C7A5B), Offset(w * 0.45f, h * 0.50f), Size(w * 0.10f, h * 0.14f))
                // TV Frame
                drawRoundRect(Color(0xFF2D3142), Offset(w * 0.10f, h * 0.06f), Size(w * 0.80f, h * 0.48f), CornerRadius(12f, 12f))
                // Pastel glowing screen
                drawRoundRect(
                    Brush.linearGradient(listOf(OurWorldSky, OurWorldPeach)),
                    Offset(w * 0.14f, h * 0.10f),
                    Size(w * 0.72f, h * 0.40f),
                    CornerRadius(8f, 8f)
                )
            }

            "plant" -> {
                drawOval(Color.Black.copy(alpha = 0.10f), Offset(w * 0.20f, h * 0.88f), Size(w * 0.60f, h * 0.08f))
                // Leaves
                drawOval(Color(0xFF52B788), Offset(w * 0.14f, h * 0.16f), Size(w * 0.36f, h * 0.32f))
                drawOval(Color(0xFF40916C), Offset(w * 0.48f, h * 0.18f), Size(w * 0.36f, h * 0.32f))
                drawOval(Color(0xFF74C69D), Offset(w * 0.30f, h * 0.04f), Size(w * 0.40f, h * 0.38f))
                // Stems
                drawLine(Color(0xFF2D6A4F), Offset(w * 0.5f, h * 0.28f), Offset(w * 0.5f, h * 0.60f), strokeWidth = 5f)
                // Terracotta pot
                val pot = Path().apply {
                    moveTo(w * 0.24f, h * 0.58f)
                    lineTo(w * 0.76f, h * 0.58f)
                    lineTo(w * 0.68f, h * 0.92f)
                    lineTo(w * 0.32f, h * 0.92f)
                    close()
                }
                drawPath(pot, color = Color(0xFFD97757))
                drawRoundRect(Color(0xFFE08B6D), Offset(w * 0.20f, h * 0.54f), Size(w * 0.60f, h * 0.10f), CornerRadius(6f, 6f))
            }

            "candle" -> {
                val haloAlpha = (if (isWarmLighting) 0.48f else 0.28f) * glowPulse
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFD166).copy(alpha = haloAlpha), Color.Transparent),
                        center = Offset(w * 0.5f, h * 0.28f),
                        radius = w * 0.95f
                    ),
                    radius = w * 0.95f,
                    center = Offset(w * 0.5f, h * 0.28f)
                )
                // Candle body
                drawRoundRect(OurWorldSoftWhite, Offset(w * 0.32f, h * 0.40f), Size(w * 0.36f, h * 0.44f), CornerRadius(6f, 6f))
                drawRoundRect(Color(0xFFB08968), Offset(w * 0.24f, h * 0.80f), Size(w * 0.52f, h * 0.12f), CornerRadius(6f, 6f))
                // Flame
                drawOval(Color(0xFFFF9F1C), Offset(w * 0.41f, h * 0.18f), Size(w * 0.18f, h * 0.22f))
                drawOval(Color(0xFFFFF3B0), Offset(w * 0.44f, h * 0.24f), Size(w * 0.12f, h * 0.14f))
            }

            "frame" -> {
                // Wooden picture frame
                drawRoundRect(Color(0xFFB08968), Offset(0f, 0f), Size(w, h), CornerRadius(10f, 10f))
                drawRoundRect(OurWorldCream, Offset(w * 0.10f, h * 0.12f), Size(w * 0.80f, h * 0.76f), CornerRadius(4f, 4f))
                // Pastel sunset landscape inside frame
                drawRoundRect(OurWorldPeach, Offset(w * 0.14f, h * 0.16f), Size(w * 0.72f, h * 0.68f), CornerRadius(4f, 4f))
                drawCircle(OurWorldRose, radius = w * 0.10f, center = Offset(w * 0.50f, h * 0.44f))
            }

            "rug" -> {
                drawOval(OurWorldMint.copy(alpha = 0.75f), Offset(0f, 0f), Size(w, h))
                drawOval(OurWorldSoftWhite.copy(alpha = 0.7f), Offset(w * 0.08f, h * 0.14f), Size(w * 0.84f, h * 0.72f), style = Stroke(width = 4f))
            }

            "clock" -> {
                drawCircle(Color(0xFFB08968), radius = w * 0.48f, center = Offset(w * 0.5f, h * 0.5f))
                drawCircle(OurWorldSoftWhite, radius = w * 0.40f, center = Offset(w * 0.5f, h * 0.5f))
                drawLine(OurWorldDarkBrown, Offset(w * 0.5f, h * 0.5f), Offset(w * 0.5f, h * 0.22f), strokeWidth = 4f, cap = StrokeCap.Round)
                drawLine(OurWorldRose, Offset(w * 0.5f, h * 0.5f), Offset(w * 0.72f, h * 0.5f), strokeWidth = 3f, cap = StrokeCap.Round)
            }

            "guitar" -> {
                drawOval(Color.Black.copy(alpha = 0.10f), Offset(w * 0.20f, h * 0.90f), Size(w * 0.60f, h * 0.08f))
                // Neck
                drawRoundRect(Color(0xFF7F5539), Offset(w * 0.42f, h * 0.04f), Size(w * 0.16f, h * 0.46f), CornerRadius(4f, 4f))
                // Body
                drawCircle(Color(0xFFDDA15E), radius = w * 0.28f, center = Offset(w * 0.5f, h * 0.54f))
                drawCircle(Color(0xFFDDA15E), radius = w * 0.36f, center = Offset(w * 0.5f, h * 0.72f))
                drawCircle(Color(0xFF5C3A1E), radius = w * 0.12f, center = Offset(w * 0.5f, h * 0.60f))
            }
        }
    }
}
