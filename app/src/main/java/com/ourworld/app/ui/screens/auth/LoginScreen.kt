package com.ourworld.app.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourworld.app.ui.components.FloatingHearts
import com.ourworld.app.ui.components.OurWorldButton
import com.ourworld.app.ui.components.OurWorldTextField
import com.ourworld.app.ui.theme.*
import com.ourworld.app.ui.viewmodel.AuthUiState

@Composable
fun LoginScreen(
    uiState: AuthUiState,
    onLogin: (String, String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onClearError: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Logo area
            Text("🏠", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "OurWorld",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = OurWorldRose
            )
            Text(
                "Your cozy shared home",
                fontSize = 16.sp,
                color = OurWorldWarmBrown,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Login form
            OurWorldTextField(
                value = email,
                onValueChange = { email = it; onClearError() },
                label = "Email",
                leadingIcon = Icons.Outlined.Email
            )
            Spacer(modifier = Modifier.height(16.dp))
            OurWorldTextField(
                value = password,
                onValueChange = { password = it; onClearError() },
                label = "Password",
                isPassword = true,
                leadingIcon = Icons.Outlined.Lock
            )

            // Error message
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
                text = "Welcome Home 💕",
                onClick = { onLogin(email, password) },
                isLoading = uiState.isLoading,
                enabled = email.isNotBlank() && password.isNotBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text(
                    "Don't have a home yet? Create one ✨",
                    color = OurWorldRose,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
