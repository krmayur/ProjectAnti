package com.ourworld.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val OurWorldColorScheme = lightColorScheme(
    primary = OurWorldRose,
    onPrimary = OurWorldSoftWhite,
    primaryContainer = OurWorldPeach,
    onPrimaryContainer = OurWorldDarkBrown,
    secondary = OurWorldLavender,
    onSecondary = OurWorldDarkBrown,
    secondaryContainer = OurWorldLavender.copy(alpha = 0.3f),
    onSecondaryContainer = OurWorldDarkBrown,
    tertiary = OurWorldMint,
    onTertiary = OurWorldDarkBrown,
    tertiaryContainer = OurWorldMint.copy(alpha = 0.3f),
    onTertiaryContainer = OurWorldDarkBrown,
    background = OurWorldCream,
    onBackground = OurWorldDarkBrown,
    surface = OurWorldSoftWhite,
    onSurface = OurWorldDarkBrown,
    surfaceVariant = OurWorldLightGray,
    onSurfaceVariant = OurWorldWarmBrown,
    outline = OurWorldLightRose,
    error = OurWorldError,
    onError = OurWorldSoftWhite
)

@Composable
fun OurWorldTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = OurWorldCream.toArgb()
            window.navigationBarColor = OurWorldCream.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = OurWorldColorScheme,
        typography = OurWorldTypography,
        content = content
    )
}
