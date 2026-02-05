package com.amos_tech_code.weatherforecast.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

val DarkBlueStart = Color(0xFF2E335A)
val DarkBlueEnd = Color(0xFF1C1B33)

val PurpleStart = Color(0xFF5936B4)
val PurpleEnd = Color(0xFF362A84)

val BluePurpleStart = Color(0xFF3658B1)
val BluePurpleEnd = Color(0xFFC159EC)

val LightBlueStart = Color(0xFFAEC9FF)
val LightBlueEnd = Color(0xFF083072)

val PinkPurpleStart = Color(0xFFF7CBFD)
val PinkPurpleEnd = Color(0xFF7758D1)

val SolidPurple = Color(0xFF48319D)
val SolidDarkBlue = Color(0xFF1F1D47)
val SolidBrightPurple = Color(0xFFC427FB)
val SolidLightPurple = Color(0xFFE0D9FF)

// Text Colors
val TextPrimaryLight = Color(0xFF000000)
val TextSecondaryLight = Color(0xFF3C3C43)
val TextTertiaryLight = Color(0xFF3C3C43)
val TextQuaternaryLight = Color(0xFF3C3C43)

val TextPrimaryDark = Color(0xFFFFFFFF)
val TextSecondaryDark = Color(0xFFEBEBF5)
val TextTertiaryDark = Color(0xFFEBEBF5)
val TextQuaternaryDark = Color(0xFFEBEBF5)

// Gradients as Brushes
val DarkBackgroundGradient = Brush.verticalGradient(
    colors = listOf(DarkBlueStart, DarkBlueEnd)
)

val CardPurpleGradient = Brush.linearGradient(
    colors = listOf(PurpleStart, PurpleEnd)
)

val CardBluePurpleGradient = Brush.linearGradient(
    colors = listOf(BluePurpleStart, BluePurpleEnd)
)

val CardLightBlueGradient = Brush.linearGradient(
    colors = listOf(LightBlueStart, LightBlueEnd)
)

val CardRadialPinkPurple = Brush.radialGradient(
    colors = listOf(PinkPurpleStart, PinkPurpleEnd)
)