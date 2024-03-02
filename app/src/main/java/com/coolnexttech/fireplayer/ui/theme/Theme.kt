package com.coolnexttech.fireplayer.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun FirePlayerTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = AppColors.textColor,
        background = AppColors.background,
        surface = AppColors.alternateBackground,
        secondaryContainer = AppColors.secondaryContainer,
        onSurface = AppColors.textColor
    )

    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}