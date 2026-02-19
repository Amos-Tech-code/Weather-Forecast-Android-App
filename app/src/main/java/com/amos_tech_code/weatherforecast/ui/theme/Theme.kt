package com.amos_tech_code.weatherforecast.ui.theme

import android.app.Activity
import android.os.Build
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
import androidx.core.view.WindowCompat

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
    // Set system bar colors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // By setting the second parameter to 'false', we are telling the system that our app will handle the colors.
            WindowCompat.setDecorFitsSystemWindows(window, false)

            // Set both status to be transparent and navigation bar to be solid dark blue to draw behind them
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = SolidDarkBlue.toArgb()

            // Handle the appearance of the status and navigation bar icons
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = false
            insetsController.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}