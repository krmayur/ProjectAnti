package com.ourworld.app.ui.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Pair : Screen("pair")
    data object Home : Screen("home")
    data object Garden : Screen("garden")
    data object Activities : Screen("activities")
    data object Gifts : Screen("gifts")
    data object Memories : Screen("memories")
    data object AvatarCustomize : Screen("avatar_customize")
}
