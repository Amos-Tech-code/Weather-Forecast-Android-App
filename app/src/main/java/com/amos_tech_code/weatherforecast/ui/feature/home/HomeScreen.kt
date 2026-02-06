package com.amos_tech_code.weatherforecast.ui.feature.home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.amos_tech_code.weatherforecast.R
import com.amos_tech_code.weatherforecast.core.util.ObserveAsEvents
import com.amos_tech_code.weatherforecast.ui.navigation.AddCityRoute
import com.amos_tech_code.weatherforecast.ui.navigation.HomeRoute
import com.amos_tech_code.weatherforecast.ui.navigation.WeatherListRoute
import com.amos_tech_code.weatherforecast.ui.theme.AppTypography
import com.amos_tech_code.weatherforecast.ui.theme.DarkBackgroundGradient
import com.amos_tech_code.weatherforecast.ui.theme.SolidBrightPurple
import com.amos_tech_code.weatherforecast.ui.theme.SolidPurple
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = koinViewModel()
) {

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            is HomeEvent.ShowErrorMessage -> {
                Toast.makeText(navController.context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )
    val isSheetPartiallyExpanded = scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded
    val selectedTab = remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // 1. Background Image - Full screen including behind system bars
        Image(
            painter = painterResource(id = R.drawable.ic_weather_background_night),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop// Changed to Crop for better results
        )

        when (val homeState = state) {
            is HomeState.Success -> {
                val data = homeState.data

                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = 320.dp, // Adjusted to show tabs + hourly section
                    sheetContainerColor = Color.Transparent,
                    sheetDragHandle = null, // We'll use a custom one inside the sheet
                    sheetShadowElevation = 0.dp,
                    containerColor = Color.Transparent,
                    sheetContent = {
                        BottomSheetContent(weatherData = data)
                    }
                ) {
                    // Main Background Content (Location + House)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                            .statusBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TopWeatherInfo(data)

                        Spacer(modifier = Modifier.height(60.dp))

                        Image(
                            painter = painterResource(id = R.drawable.ic_house),
                            contentDescription = null,
                            modifier = Modifier
                                .size(300.dp)
                                .scale(1.2f)
                        )
                    }
                }
            }

            is HomeState.Loading -> LoadingView()
            is HomeState.Error -> ErrorView(message = homeState.message, onRetry = {})
        }

        // 2. Fixed Bottom Bar at the very bottom
        AnimatedVisibility(
            visible = isSheetPartiallyExpanded,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            CustomBottomAppBar(
                selectedTab = selectedTab.intValue,
                onTabSelected = { tabIndex ->
                    when (tabIndex) {
                        0 -> navController.navigate(HomeRoute)

                        1 -> navController.navigate(AddCityRoute)

                        2 -> navController.navigate(WeatherListRoute)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .systemBarsPadding()
            )
        }

    }

}

@Composable
private fun TopWeatherInfo(weatherData: WeatherData) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Location
        Text(
            text = weatherData.location,
            style = AppTypography.title1Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Temperature and condition
        Text(
            text = "${weatherData.currentTemp}°",
            style = TextStyle(
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = weatherData.condition,
            style = AppTypography.bodyBold,
            color = Color.White.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "H:${weatherData.highTemp}° L:${weatherData.lowTemp}°",
            style = AppTypography.caption1,
            color = Color.White.copy(alpha = 0.6f)
        )

    }
}

@Composable
private fun BottomSheetContent(
    weatherData: WeatherData,
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2E335A).copy(alpha = 0.9f)) // Solid dark purple background
            .verticalScroll(rememberScrollState()) // Allows the grid to be scrolled up
    ) {
        // Custom Drag Handle
        Box(Modifier
            .fillMaxWidth()
            .padding(top = 8.dp), contentAlignment = Alignment.Center) {
            Box(Modifier
                .size(40.dp, 4.dp)
                .background(Color.Black.copy(0.3f), CircleShape))
        }

        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Hourly Forecast",
                color = if(selectedTab == 0) Color.White else Color.White.copy(0.5f),
                modifier = Modifier.clickable { selectedTab = 0 })
            Text("Weekly Forecast",
                color = if(selectedTab == 1) Color.White else Color.White.copy(0.5f),
                modifier = Modifier.clickable { selectedTab = 1 })
        }

        HorizontalDivider(color = Color.White.copy(0.1f))

        // Hourly/Weekly horizontal section (Always visible in peek)
        LazyRow(
            contentPadding = PaddingValues(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (selectedTab == 0) {
                items(weatherData.hourlyForecast) { ForecastCard(it, it.time == "Now") }
            } else {
                // Now Weekly is also a LazyRow with the same card style
                items(weatherData.weeklyForecast) { day ->
                    // Mapping DailyForecast to the card format
                    ForecastCard(HourlyForecast(day.day, day.highTemp, day.iconRes, day.precipitation))
                }
            }
        }

        // Weather Details Grid (Visible when user scrolls/expands)
        WeatherDetailsGrid(weatherData.weatherDetails)

        // Extra spacer for the bottom bar
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun ForecastCard(
    forecast: HourlyForecast,
    isActive: Boolean = false
) {
    // Determine background and border based on selection
    val backgroundColor = if (isActive) SolidPurple else Color(0xFF48319D).copy(alpha = 0.3f)
    val borderColor = if (isActive) Color.White.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.2f)

    Surface(
        modifier = Modifier
            .width(60.dp)
            .height(146.dp),
        shape = RoundedCornerShape(50.dp), // Full stadium shape
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = if (isActive) 10.dp else 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Time
            Text(
                text = forecast.time,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                ),
                color = Color.White
            )

            // 2. Weather Icon & Probability
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = forecast.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )

                // Precipitation percentage (if available)
                if (forecast.precipitation != null) {
                    Text(
                        text = "${forecast.precipitation}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF40CBD8), // Light blue from design
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // 3. Temperature
            Text(
                text = "${forecast.temp}°",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = Color.White
            )
        }
    }
}

@Composable
private fun WeatherDetailsGrid(details: WeatherDetails) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AirQualityCard(
            title = "AIR QUALITY",
            value = details.airQuality.riskLevel,
            progress = 0.3f
        )

        // UV Index and Sunrise Row
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            UVIndexCard(
                modifier = Modifier.weight(1f),
                uvValue = details.uvIndex.value.toFloat(), // Now dynamic
                level = details.uvIndex.level
            )
            SunriseCard(
                modifier = Modifier.weight(1f),
                sunrise = details.sunrise,
                sunset = details.sunset
            )
        }

        // Wind and Rainfall Row
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            WindCard(modifier = Modifier.weight(1f), speed = details.wind.speed, direction = details.wind.direction, unit = "km/h")
            WeatherDetailCard(
                modifier = Modifier.weight(1f),
                title = "RAINFALL",
                value = details.rainfall.current,
                subtitle = "in last hour\n ${details.rainfall.next24h} expected in next 24h."
            )
        }

        // Feels Like and Humidity Row
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            WeatherDetailCard(modifier = Modifier.weight(1f), title = "FEELS LIKE", value = details.feelsLike.toString(), subtitle = "Similar to the actual temperature.")
            WeatherDetailCard(modifier = Modifier.weight(1f), title = "HUMIDITY", value = details.humidity.toString(), subtitle = "The dew point is 17 right now.")
        }

        // Visibility and Pressure Row
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            WeatherDetailCard(modifier = Modifier.weight(1f), title = "VISIBILITY", value = details.visibility, subtitle = "Similar to actual temperature.")
            PressureCard(modifier = Modifier.weight(1f), value = details.pressure)
        }
    }
}

@Composable
private fun WeatherDetailCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: Int? = null
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp),
        color = Color(0xFF48319D).copy(alpha = 0.2f), // Semi-transparent
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(painterResource(id = icon), null, tint = Color.White.copy(0.6f), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                }
                Text(text = title, style = AppTypography.caption2, color = Color.White.copy(alpha = 0.6f))
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, style = AppTypography.title2Bold, color = Color.White)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = subtitle, style = AppTypography.caption2, color = Color.White, lineHeight = 16.sp)
        }
    }
}


@Composable
fun CustomBottomAppBar(
    modifier: Modifier = Modifier,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val indicatorPainter = painterResource(id = R.drawable.ic_bg_indicator)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp) // Set a fixed height for the whole component
    ) {
        // 1. --- CURVE CORRECTION ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // The highest point of the "wings" on the side
            val wingHeight = height * 0.3f

            // Define the path for the new smooth curve
            val path = Path().apply {
                // Start at the top-left, where the curve begins
                moveTo(0f, wingHeight)

                // --- FIX IS HERE ---
                // The main curve across the top, now bending downwards
                quadraticTo(
                    x1 = width / 2,    // The control point is at the horizontal center...
                    y1 = height * 0.8f, // ...and pushed DOWN to create the dip in the middle.
                    x2 = width,        // The curve ends at the top-right edge
                    y2 = wingHeight
                )

                // Line down to the bottom-right corner
                lineTo(width, height)
                // Line to the bottom-left corner
                lineTo(0f, height)
                // Close the path to complete the shape
                close()
            }

            // Draw the background gradient (no changes here)
            drawPath(
                path = path,
                brush = DarkBackgroundGradient
            )

            // Draw the top border highlight (no changes here)
            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.2f),
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // 2. --- ICONS AND INDICATORS ---
        // This part needs adjustment to fit the new curve shape.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // The side icons are now pushed down a bit
            NavigationItem(
                icon = R.drawable.ic_location,
                isSelected = selectedTab == 0,
                indicator = indicatorPainter,
                onClick = { onTabSelected(0) },
                modifier = Modifier.offset(y = (-10).dp) // Adjust offset for new curve
            )

            // Center Button now sits lower, in the dip
            PlusButton(
                isSelected = selectedTab == 1,
                indicator = indicatorPainter,
                onClick = {
                    onTabSelected(1)
                },
                modifier = Modifier.offset(y = 8.dp) // Nudge down into the dip
            )

            NavigationItem(
                icon = R.drawable.ic_list,
                isSelected = selectedTab == 2,
                indicator = indicatorPainter,
                onClick = { onTabSelected(2) },
                modifier = Modifier.offset(y = (-10).dp) // Adjust offset for new curve
            )
        }
    }
}

@Composable
private fun NavigationItem(
    modifier: Modifier = Modifier,
    icon: Int,
    isSelected: Boolean,
    indicator: Painter,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(70.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
//        if (isSelected) {
//            Image(
//                painter = indicator,
//                contentDescription = "Selected tab indicator",
//                modifier = Modifier.matchParentSize(),
//                contentScale = ContentScale.Fit
//            )
//        }
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
private fun PlusButton(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    indicator: Painter,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        //if (isSelected) {
            Image(
                painter = indicator,
                contentDescription = "Selected tab indicator",
                modifier = Modifier.size(220.dp, 100.dp),
            )
        //}
        Surface(
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 10.dp,
            tonalElevation = 10.dp,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
            modifier = Modifier
                .size(60.dp)
                .shadow(10.dp, CircleShape)
                .clickable(onClick = onClick)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_plus),
                contentDescription = "Add new city",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .size(44.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Error",
                style = AppTypography.title2Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = AppTypography.body,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SolidBrightPurple
                )
            ) {
                Text("Retry")
            }
        }
    }
}
