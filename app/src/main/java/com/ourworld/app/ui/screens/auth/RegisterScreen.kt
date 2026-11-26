package com.ourworld.app.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
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
fun RegisterScreen(
    uiState: AuthUiState,
    onRegister: (String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onClearError: () -> Unit
) {
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

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
            Spacer(modifier = Modifier.height(40.dp))

            Text("🏡", fontSize = 56.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Build Your Home",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = OurWorldRose
            )
            Text(
                "Create your cozy space together",
                fontSize = 14.sp,
                color = OurWorldWarmBrown,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            OurWorldTextField(
                value = displayName,
                onValueChange = { displayName = it; onClearError() },
                label = "Your Name",
                leadingIcon = Icons.Outlined.Person
            )
            Spacer(modifier = Modifier.height(12.dp))
            OurWorldTextField(
                value = email,
                onValueChange = { email = it; onClearError() },
                label = "Email",
                leadingIcon = Icons.Outlined.Email
            )
            Spacer(modifier = Modifier.height(12.dp))
            OurWorldTextField(
                value = password,
                onValueChange = { password = it; onClearError() },
                label = "Password",
                isPassword = true,
                leadingIcon = Icons.Outlined.Lock
            )
            Spacer(modifier = Modifier.height(12.dp))
            OurWorldTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; onClearError() },
                label = "Confirm Password",
                isPassword = true,
                leadingIcon = Icons.Outlined.Lock
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
                text = "Create My Home 🏠",
                onClick = {
                    if (password == confirmPassword) {
                        onRegister(email, password, displayName)
                    }
                },
                isLoading = uiState.isLoading,
                enabled = email.isNotBlank() && password.isNotBlank() &&
                    displayName.isNotBlank() && password == confirmPassword
            )

            if (password.isNotBlank() && confirmPassword.isNotBlank() && password != confirmPassword) {
                Text(
                    "Passwords don't match 💔",
                    color = OurWorldError,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text(
                    "Already have a home? Come back 💕",
                    color = OurWorldRose,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
