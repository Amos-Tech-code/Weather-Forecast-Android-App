package com.amos_tech_code.weatherforecast.ui.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amos_tech_code.weatherforecast.ui.theme.AppTypography

@Composable
fun WeatherDetailCardContainer(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.height(160.dp),
        color = Color(0xFF48319D).copy(alpha = 0.2f),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = AppTypography.caption2, color = Color.White.copy(alpha = 0.6f))
            if (value.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(value, style = AppTypography.title2Bold, color = Color.White)
            }
            Spacer(Modifier.height(24.dp))
            content()
        }
    }
}

@Composable
fun AirQualityCard(
    title: String,
    value: String,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp),
        color = Color(0xFF48319D).copy(alpha = 0.2f), // Glass background
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Grain, // Your icon
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }

            // Main Value
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = Color.White
            )

            // Custom Gradient Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Canvas(modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)) {
                    val trackHeight = size.height
                    // Draw Gradient Track
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF3254C8), // Blue
                                Color(0xFF7150C2), // Purple
                                Color(0xFFE64392)  // Pink
                            )
                        ),
                        size = size,
                        cornerRadius = CornerRadius(trackHeight / 2)
                    )

                    // Draw the white Thumb
                    val thumbRadius = 4.dp.toPx()
                    val xPos = size.width * progress
                    drawCircle(
                        color = Color.White,
                        radius = thumbRadius,
                        center = Offset(xPos, size.height / 2)
                    )
                }
            }

            // Footer "See more" Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "See more",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun UVIndexCard(
    modifier: Modifier = Modifier,
    uvValue: Float,
    level: String
) {
    // Normalize UV value (0-11) to a 0.0 to 1.0 float for the progress bar
    val progress = (uvValue / 11f).coerceIn(0f, 1f)

    WeatherDetailCardContainer(
        modifier = modifier,
        title = "UV INDEX",
        value = "${uvValue.toInt()}"
    ) {
        Text(
            text = level,
            style = AppTypography.title3Bold,
            color = Color.White
        )

        Spacer(Modifier.height(12.dp))

        // UV Gradient Bar with Thumb
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Canvas(modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)) {
                // 1. Draw the Gradient Track
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF3254C8), // Low (Blue)
                            Color(0xFF7150C2), // Moderate (Purple)
                            Color(0xFFE64392)  // High (Pink)
                        )
                    ),
                    size = size,
                    cornerRadius = CornerRadius(size.height / 2)
                )

                // 2. Draw the white Thumb based on uvValue
                val thumbRadius = 3.dp.toPx()
                val xPos = size.width * progress

                drawCircle(
                    color = Color.White,
                    radius = thumbRadius,
                    center = Offset(xPos, size.height / 2)
                )
            }
        }
    }
}

@Composable
fun SunriseCard(modifier: Modifier, sunrise: String, sunset: String) {
    WeatherDetailCardContainer(modifier, title = "SUNRISE", value = sunrise) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Draw the horizontal baseline
                drawLine(Color.White.copy(0.2f), Offset(0f, height/2), Offset(width, height/2), strokeWidth = 2f)

                // Draw the curve
                val path = Path().apply {
                    moveTo(0f, height / 2)
                    cubicTo(width * 0.25f, 0f, width * 0.75f, height, width, height / 2)
                }
                drawPath(path, color = Color(0xFF6397FF), style = Stroke(width = 4f))

                // Draw current position dot (the thumb)
                drawCircle(Color.White, radius = 6f, center = Offset(width * 0.3f, height * 0.25f))
            }
        }
        Text("Sunset: $sunset", style = AppTypography.caption2, color = Color.White)
    }
}

@Composable
fun WindCard(modifier: Modifier, speed: String, direction: String? = null, unit: String) {
    WeatherDetailCardContainer(modifier, title = "WIND", value = "") {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            val compassSize = 100.dp // Define a single size for the compass

            // Compass Circles
            Canvas(modifier = Modifier.size(compassSize)) {
                // Outer circle
                drawCircle(
                    color = Color.White.copy(0.1f),
                    style = Stroke(width = 1.dp.toPx())
                )
                // Inner circle
                drawCircle(
                    color = Color.White.copy(0.05f),
                    radius = size.width / 3,
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Cardinal Directions positioned around the compass
            val cardinalPadding = (compassSize / 2) + 8.dp
            Text(
                "N",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(bottom = cardinalPadding),
                color = Color.White,
                fontSize = 10.sp
            )
            Text(
                "S",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(top = cardinalPadding),
                color = Color.White,
                fontSize = 10.sp
            )
            Text(
                "W",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(end = cardinalPadding),
                color = Color.White,
                fontSize = 10.sp
            )
            Text(
                "E",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(start = cardinalPadding),
                color = Color.White,
                fontSize = 10.sp
            )


            // Stack the Speed and Arrow inside their own Box for proper alignment
            Box(contentAlignment = Alignment.Center) {
                // Center Speed
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(speed, style = AppTypography.title3Bold, color = Color.White)
                    Text(unit, style = AppTypography.caption2, color = Color.White.copy(0.6f))
                }

                // Wind Arrow Indicator
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = "Wind direction",
                    tint = Color.White.copy(0.6f), // Use a contrasting color to make it visible
                    modifier = Modifier
                        .size(50.dp) // Make the arrow container the size of the compass
                        .rotate(45f) // The rotation determines the direction
                )
            }
        }
    }
}

@Composable
fun PressureCard(
    modifier: Modifier,
    value: String
) {
    WeatherDetailCardContainer(modifier, title = "PRESSURE", value = "") {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(80.dp)) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.width / 2
                val dashCount = 50
                val angleStep = 360f / dashCount

                // Draw the dashed circular track
                for (i in 0 until dashCount) {
                    val angleInDegrees = i * angleStep
                    val angleInRadians = Math.toRadians(angleInDegrees.toDouble()).toFloat()

                    val start = Offset(
                        x = center.x + (radius - 10f) * kotlin.math.cos(angleInRadians),
                        y = center.y + (radius - 10f) * kotlin.math.sin(angleInRadians)
                    )
                    val end = Offset(
                        x = center.x + radius * kotlin.math.cos(angleInRadians),
                        y = center.y + radius * kotlin.math.sin(angleInRadians)
                    )

                    // Highlight the "active" portion of the pressure gauge
                    val color = if (i in 35..45) Color.White else Color.White.copy(0.2f)
                    val stroke = if (i in 35..45) 4f else 2f

                    drawLine(color, start, end, strokeWidth = stroke)
                }
            }
            // Central Value
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(value, style = AppTypography.title3Bold, color = Color.White)
                Text("hPa", style = AppTypography.caption2, color = Color.White.copy(0.6f))
            }
        }
    }
}
