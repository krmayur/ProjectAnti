package com.ourworld.app.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourworld.app.ui.components.CozyCard
import com.ourworld.app.ui.components.FloatingHearts
import com.ourworld.app.ui.components.OurWorldButton
import com.ourworld.app.ui.components.OurWorldTextField
import com.ourworld.app.ui.theme.*
import com.ourworld.app.ui.viewmodel.AuthUiState

@Composable
fun PairScreen(
    uiState: AuthUiState,
    onPair: (String) -> Unit,
    onShareCode: (String) -> Unit,
    onLogout: () -> Unit,
    onClearError: () -> Unit
) {
    var partnerCode by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current

    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulse by pulseAnim.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OurWorldCream)
    ) {
        FloatingHearts()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text("💑", fontSize = 56.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Find Your Partner",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = OurWorldRose
            )
            Text(
                "Share your code or enter theirs",
                fontSize = 14.sp,
                color = OurWorldWarmBrown
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Your pairing code
            CozyCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Your Pairing Code",
                    fontSize = 14.sp,
                    color = OurWorldWarmBrown,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = uiState.pairingCode,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = OurWorldRose,
                    textAlign = TextAlign.Center,
                    letterSpacing = 8.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(pulse)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(uiState.pairingCode))
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OurWorldRose)
                    ) {
                        Icon(Icons.Outlined.ContentCopy, "Copy", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy")
                    }
                    OutlinedButton(
                        onClick = { onShareCode(uiState.pairingCode) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OurWorldRose)
                    ) {
                        Icon(Icons.Outlined.Share, "Share", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = OurWorldLightRose)
                Text(
                    "  or enter partner's code  ",
                    fontSize = 13.sp,
                    color = OurWorldWarmBrown
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = OurWorldLightRose)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Enter partner code
            OurWorldTextField(
                value = partnerCode,
                onValueChange = { partnerCode = it.uppercase().take(6); onClearError() },
                label = "Partner's Code"
            )

            AnimatedVisibility(visible = uiState.error != null) {
                Text(
                    text = uiState.error ?: "",
                    color = OurWorldError,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OurWorldButton(
                text = "Connect With Partner 💕",
                onClick = { onPair(partnerCode) },
                isLoading = uiState.isLoading,
                enabled = partnerCode.length == 6
            )

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(onClick = onLogout) {
                Text("Sign Out", color = OurWorldWarmBrown, fontSize = 14.sp)
            }
        }
    }
}
