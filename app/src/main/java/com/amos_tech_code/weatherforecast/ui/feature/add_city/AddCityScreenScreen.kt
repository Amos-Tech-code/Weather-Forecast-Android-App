package com.amos_tech_code.weatherforecast.ui.feature.add_city

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.amos_tech_code.weatherforecast.R
import com.amos_tech_code.weatherforecast.core.util.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel


@Composable
fun AddCityScreen(
    navController: NavController,
    viewModel: AddCityScreenViewModel = koinViewModel()
) {
    val cities by viewModel.state.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            is AddCityScreenEvent.ShowErrorMessage -> {
                // Handle error message
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF2E335A), Color(0xFF1C1B33))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = Color.White,
                    )
                }
                Text("Weather", style = TextStyle(fontSize = 28.sp, color = Color.White))

                IconButton(
                    onClick = {
                    // TODO : (Not yet implemented)
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.White)
                }
            }

            // Search Bar
            StyledSearchBar(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryUpdated(it) },
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Scrollable List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(25.dp),
                contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp)
            ) {
                items(cities, key = { it.id}) { city ->
                    CityWeatherCard(city)
                }
            }
        }
    }
}

@Composable
fun StyledSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp), // Standard height from the design
        textStyle = TextStyle(color = Color.White, fontSize = 17.sp),
        cursorBrush = SolidColor(Color.White),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFF1C1B33).copy(alpha = 0.5f), // Dark semi-transparent
                        shape = RoundedCornerShape(10.dp)
                    )
                    .drawBehind {
                        val strokeWidth = 1.dp.toPx()
                        val cornerRadius = 10.dp.toPx()

                        // 1. Top/Inner shadow effect (subtle darken)
                        drawRoundRect(
                            color = Color.Black.copy(alpha = 0.2f),
                            size = size,
                            cornerRadius = CornerRadius(cornerRadius),
                            style = Stroke(width = strokeWidth)
                        )

                        // 2. Light outer border (glass-morphism)
                        drawRoundRect(
                            color = Color.White.copy(alpha = 0.1f),
                            size = size,
                            cornerRadius = CornerRadius(cornerRadius),
                            style = Stroke(width = 0.5.dp.toPx())
                        )
                    }
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search, // Replace with your search icon
                        contentDescription = null,
                        tint = Color(0xFFEBEBF5).copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (value.isEmpty()) {
                        Text(
                            text = "Search for a city or airport",
                            style = TextStyle(
                                color = Color(0xFFEBEBF5).copy(alpha = 0.6f),
                                fontSize = 17.sp
                            )
                        )
                    }
                    innerTextField()
                }
            }
        }
    )
}

@Composable
fun CityWeatherCard(city: CityWeather) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(184.dp)
    ) {
        // 1. The Polygon Background
        Image(
            painter = painterResource(id = R.drawable.ic_polygon),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // 2. Weather Info (Left Side)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 20.dp)
        ) {
            Text(
                text = "${city.temp}°",
                style = TextStyle(fontSize = 64.sp, color = Color.White, fontWeight = FontWeight.Normal)
            )
            Text(
                text = "H:${city.high}°  L:${city.low}°",
                style = TextStyle(fontSize = 13.sp, color = Color.White.copy(alpha = 0.6f))
            )
            Text(
                text = "${city.city}, ${city.country}",
                style = TextStyle(fontSize = 17.sp, color = Color.White)
            )
        }

        // 3. 3D Illustration (Overlapping the top-right)
        Image(
            painter = painterResource(id = city.iconRes),
            contentDescription = null,
            modifier = Modifier
                .size(160.dp)
                .align(Alignment.TopEnd)
                .offset(x = 10.dp, y = (-20).dp) // Important for that "popping out" effect
        )

        // 4. Condition Text (Bottom Right)
        Text(
            text = city.condition,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp),
            style = TextStyle(fontSize = 13.sp, color = Color.White)
        )
    }
}
