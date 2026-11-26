package com.ourworld.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ourworld.app.ui.components.OurWorldBottomNav
import com.ourworld.app.ui.navigation.OurWorldNavGraph
import com.ourworld.app.ui.navigation.Screen
import com.ourworld.app.ui.theme.OurWorldTheme
import com.ourworld.app.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OurWorldTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = hiltViewModel()
                val authState by authViewModel.uiState.collectAsState()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomNav = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.Garden.route,
                    Screen.Activities.route,
                    Screen.Gifts.route,
                    Screen.Memories.route
                )

                Scaffold(
                    bottomBar = {
                        if (showBottomNav) {
                            OurWorldBottomNav(
                                currentRoute = currentRoute ?: Screen.Home.route,
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        OurWorldNavGraph(
                            navController = navController,
                            authViewModel = authViewModel,
                            onShareCode = { code -> shareCode(code) }
                        )
                    }
                }
            }
        }
    }

    private fun shareCode(code: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                "Come join our cozy home on OurWorld! 🏠💕 Use our couple pairing code: $code"
            )
        }
        startActivity(Intent.createChooser(intent, "Share OurWorld Pairing Code"))
    }
}
