package com.amos_tech_code.weatherforecast.ui.theme

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat

private val DarkColorScheme = darkColorScheme(
    primary = SolidBrightPurple,
    secondary = SolidPurple,
    tertiary = SolidLightPurple,
    background = SolidDarkBlue,
    surface = SolidDarkBlue,
    onPrimary = TextPrimaryDark,
    onSecondary = TextSecondaryDark,
    onTertiary = TextTertiaryDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
)

private val LightColorScheme = lightColorScheme(
    primary = SolidBrightPurple,
    secondary = SolidPurple,
    tertiary = SolidLightPurple,
    background = Color.White,
    surface = Color.White,
    onPrimary = TextPrimaryLight,
    onSecondary = TextSecondaryLight,
    onTertiary = TextTertiaryLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
)

@Composable
fun WeatherForecastTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Set system bar colors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Navigation Bar color
            window.navigationBarColor = SolidDarkBlue.toArgb()

            // Status Bar to transparent
            window.statusBarColor = Color.Transparent.toArgb()

            // Clear LIGHT_STATUS_BAR flag to get white icons
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()

            val insetsController = ViewCompat.getWindowInsetsController(view)

            insetsController?.isAppearanceLightStatusBars = false

            insetsController?.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}