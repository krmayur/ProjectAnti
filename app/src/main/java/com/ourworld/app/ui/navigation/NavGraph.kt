package com.ourworld.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ourworld.app.data.model.MemoryType
import com.ourworld.app.ui.screens.activities.ActivitiesScreen
import com.ourworld.app.ui.screens.auth.LoginScreen
import com.ourworld.app.ui.screens.auth.PairScreen
import com.ourworld.app.ui.screens.auth.RegisterScreen
import com.ourworld.app.ui.screens.garden.GardenScreen
import com.ourworld.app.ui.screens.gifts.GiftsScreen
import com.ourworld.app.ui.screens.home.AvatarCustomizeScreen
import com.ourworld.app.ui.screens.home.HomeScreen
import com.ourworld.app.ui.screens.memories.MemoriesScreen
import com.ourworld.app.ui.viewmodel.*

@Composable
fun OurWorldNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    onShareCode: (String) -> Unit
) {
    val authState by authViewModel.uiState.collectAsState()

    val startDestination = when {
        !authState.isLoggedIn -> Screen.Login.route
        !authState.isPaired -> Screen.Pair.route
        else -> Screen.Home.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth screens
        composable(Screen.Login.route) {
            LoginScreen(
                uiState = authState,
                onLogin = { email, password -> authViewModel.login(email, password) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onClearError = { authViewModel.clearError() }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                uiState = authState,
                onRegister = { email, password, name -> authViewModel.register(email, password, name) },
                onNavigateToLogin = { navController.popBackStack() },
                onClearError = { authViewModel.clearError() }
            )
        }

        composable(Screen.Pair.route) {
            PairScreen(
                uiState = authState,
                onPair = { code -> authViewModel.pairWithCode(code) },
                onShareCode = onShareCode,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onClearError = { authViewModel.clearError() }
            )
        }

        // Main screens
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            val homeState by homeViewModel.uiState.collectAsState()
            val coupleId = authState.currentUser?.coupleId ?: ""
            val userId = authState.currentUser?.uid ?: ""
            val partnerId = authState.currentUser?.partnerId ?: ""

            LaunchedEffect(coupleId) {
                if (coupleId.isNotEmpty()) {
                    homeViewModel.loadHome(coupleId, userId, partnerId)
                }
            }

            HomeScreen(
                uiState = homeState,
                coupleId = coupleId,
                userId = userId,
                userCoins = authState.currentUser?.coins ?: 0,
                onUpdateWallpaper = { homeViewModel.updateWallpaper(coupleId, it) },
                onUpdateLighting = { homeViewModel.updateLighting(coupleId, it) },
                onAddFurniture = { type, name, emoji -> homeViewModel.addFurniture(coupleId, type, name, emoji, userId) },
                onMoveFurniture = { id, x, y -> homeViewModel.moveFurniture(coupleId, id, x, y) },
                onRemoveFurniture = { homeViewModel.removeFurniture(coupleId, it) },
                onPlayAnimation = { homeViewModel.playAnimation(userId, it) },
                onMoveAvatar = { x, y -> homeViewModel.moveAvatar(userId, x, y) },
                onToggleCustomize = { homeViewModel.toggleCustomizing() },
                onNavigateToAvatar = { navController.navigate(Screen.AvatarCustomize.route) },
                onUnpair = { authViewModel.unpair() },
                onLogout = { authViewModel.logout() }
            )
        }

        composable(Screen.AvatarCustomize.route) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            val homeState by homeViewModel.uiState.collectAsState()
            val userId = authState.currentUser?.uid ?: ""
            val coupleId = authState.currentUser?.coupleId ?: ""
            val partnerId = authState.currentUser?.partnerId ?: ""

            LaunchedEffect(coupleId) {
                if (coupleId.isNotEmpty()) {
                    homeViewModel.loadHome(coupleId, userId, partnerId)
                }
            }

            AvatarCustomizeScreen(
                currentAvatar = homeState.myAvatar,
                userName = authState.currentUser?.displayName ?: "You",
                onSaveAvatar = { avatar ->
                    homeViewModel.saveAvatar(userId, avatar)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Garden.route) {
            val gardenViewModel: GardenViewModel = hiltViewModel()
            val gardenState by gardenViewModel.uiState.collectAsState()
            val coupleId = authState.currentUser?.coupleId ?: ""
            val userId = authState.currentUser?.uid ?: ""

            LaunchedEffect(coupleId) {
                if (coupleId.isNotEmpty()) {
                    gardenViewModel.loadGarden(coupleId)
                }
            }

            GardenScreen(
                uiState = gardenState,
                coupleId = coupleId,
                userId = userId,
                onPlantSeed = { plantType -> gardenViewModel.plantSeed(coupleId, plantType, userId) },
                onWaterPlant = { gardenViewModel.waterPlant(coupleId, it) },
                onRemovePlant = { gardenViewModel.removePlant(coupleId, it) },
                onMovePlant = { id, x, y -> gardenViewModel.movePlant(coupleId, id, x, y) },
                onHarvestPlant = { plant -> gardenViewModel.harvestPlant(coupleId, plant, userId) },
                onTogglePlanting = { gardenViewModel.togglePlanting() }
            )
        }

        composable(Screen.Activities.route) {
            val activitiesViewModel: ActivitiesViewModel = hiltViewModel()
            val activitiesState by activitiesViewModel.uiState.collectAsState()

            ActivitiesScreen(
                uiState = activitiesState
            )
        }

        composable(Screen.Gifts.route) {
            val giftsViewModel: GiftsViewModel = hiltViewModel()
            val giftsState by giftsViewModel.uiState.collectAsState()
            val coupleId = authState.currentUser?.coupleId ?: ""
            val userId = authState.currentUser?.uid ?: ""
            val partnerId = authState.currentUser?.partnerId ?: ""

            LaunchedEffect(coupleId) {
                if (coupleId.isNotEmpty()) {
                    giftsViewModel.loadGifts(coupleId)
                }
            }

            GiftsScreen(
                uiState = giftsState,
                coupleId = coupleId,
                userId = userId,
                partnerId = partnerId,
                onSendGift = { type, msg -> giftsViewModel.sendGift(coupleId, type, userId, partnerId, msg) },
                onOpenGift = { giftsViewModel.openGift(coupleId, it) },
                onSendNote = { giftsViewModel.sendNote(coupleId, userId, it) },
                onToggleSendingGift = { giftsViewModel.toggleSendingGift() },
                onToggleWritingNote = { giftsViewModel.toggleWritingNote() }
            )
        }

        composable(Screen.Memories.route) {
            val memoriesViewModel: MemoriesViewModel = hiltViewModel()
            val memoriesState by memoriesViewModel.uiState.collectAsState()
            val coupleId = authState.currentUser?.coupleId ?: ""
            val userId = authState.currentUser?.uid ?: ""

            LaunchedEffect(coupleId) {
                if (coupleId.isNotEmpty()) {
                    memoriesViewModel.loadMemories(coupleId)
                }
            }

            MemoriesScreen(
                uiState = memoriesState,
                coupleId = coupleId,
                userId = userId,
                onAddMemory = { title, desc, type -> memoriesViewModel.addMemory(coupleId, title, desc, type, userId) },
                onToggleAdding = { memoriesViewModel.toggleAddingMemory() }
            )
        }
    }

    // Handle auth state changes
    LaunchedEffect(authState.isLoggedIn, authState.isPaired) {
        when {
            !authState.isLoggedIn -> {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
            authState.isLoggedIn && !authState.isPaired -> {
                navController.navigate(Screen.Pair.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
            authState.isLoggedIn && authState.isPaired && authState.pairingSuccess -> {
                navController.navigate(Screen.Home.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }
}
