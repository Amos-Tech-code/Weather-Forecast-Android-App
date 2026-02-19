package com.amos_tech_code.weatherforecast.ui.feature.add_city

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.amos_tech_code.weatherforecast.R
import com.amos_tech_code.weatherforecast.core.util.ObserveAsEvents
import com.amos_tech_code.weatherforecast.domain.model.City
import com.amos_tech_code.weatherforecast.domain.model.CitySearchResult
import com.amos_tech_code.weatherforecast.domain.model.CityWithWeather
import com.amos_tech_code.weatherforecast.ui.components.EmptySavedCitiesView
import com.amos_tech_code.weatherforecast.ui.components.LoadingView
import com.amos_tech_code.weatherforecast.ui.navigation.AddCityRoute
import com.amos_tech_code.weatherforecast.ui.navigation.HomeRoute
import com.amos_tech_code.weatherforecast.ui.theme.AppTypography
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCityScreen(
    navController: NavController,
    viewModel: AddCityScreenViewModel = koinViewModel()
) {
    val savedCities by viewModel.savedCities.collectAsStateWithLifecycle()
    val weatherMap by viewModel.citiesWithWeather.collectAsStateWithLifecycle()
    val weatherLoadingStates by viewModel.weatherLoadingStates.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val isLoadingCities by viewModel.isLoadingCities.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            is AddCityScreenEvent.ShowErrorMessage -> {
                Toast.makeText(navController.context, event.message, Toast.LENGTH_SHORT).show()
            }
            is AddCityScreenEvent.CityAdded -> {
                // Navigate back to home with the new city
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    "selected_city",
                    event.city
                )
                navController.navigate(HomeRoute) {
                    popUpTo(AddCityRoute) { inclusive = true }
                    launchSingleTop = true
                }

            }
            is AddCityScreenEvent.CityRemoved -> {
                Toast.makeText(navController.context, "${event.cityName} removed", Toast.LENGTH_SHORT).show()
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
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header
            AddCityHeader(
                onBackClick = {
                    if (navController.previousBackStackEntry == null) {
                        (navController.context as? Activity)?.finish()
                    } else {
                        navController.navigateUp()
                    }
                },
                onMenuClick = { /* TODO */ },
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Search Bar
            StyledSearchBar(
                value = searchQuery,
                onValueChange = {
                    viewModel.onSearchQueryUpdated(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(2f)
            )

            val isCurrentlySearching = searchQuery.isNotEmpty()

            // Search Suggestions - Always in composition, visibility is toggled
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .zIndex(1f)
                    // Use graphicsLayer for performant visibility toggling
                    .graphicsLayer { alpha = if (isCurrentlySearching) 1f else 0f }
            ) {
                GlassSearchSuggestions(
                    suggestions = searchResults,
                    isLoading = isSearching,
                    onSuggestionClick = { suggestion ->
                        viewModel.onAddToSavedState(suggestion)
                    },
                )
            }

            // Saved Cities Section - Always in composition, visibility is toggled
            Column(
                modifier = Modifier
                    // Use graphicsLayer to hide this section when searching
                    .graphicsLayer { alpha = if (isCurrentlySearching) 0f else 1f }
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                if (isLoadingCities) {
                    LoadingView()
                } else if (savedCities.isEmpty()) {
                    EmptySavedCitiesView()
                } else {
                    LazyColumn(
                        // Prevent the user from scrolling the list when it's hidden
                        userScrollEnabled = !isCurrentlySearching,
                        verticalArrangement = Arrangement.spacedBy(25.dp),
                        contentPadding = PaddingValues(bottom = 40.dp)
                    ) {
                        items(savedCities, key = { it.id }) { city ->
                            val cityWithWeather = weatherMap[city.id]
                            val isWeatherLoading = weatherLoadingStates.contains(city.id)

                            CityWeatherCard(
                                city = city,
                                cityWithWeather = cityWithWeather,
                                isLoading = isWeatherLoading,
                                onClick = {
                                    navController.previousBackStackEntry?.savedStateHandle?.set(
                                        "selected_city",
                                        city
                                    )
                                    navController.navigate(HomeRoute) {
                                        popUpTo(AddCityRoute) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CityWeatherCard(
    city: City,
    cityWithWeather: CityWithWeather?,
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    val weather = cityWithWeather?.weather

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(184.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                onClick = onClick
            )
    ) {
        // The Polygon Background
        Image(
            painter = painterResource(id = R.drawable.ic_polygon),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
            alpha = if (weather != null) 0.3f else 0.15f
        )

        // City name (always shown)
        Text(
            text = "${city.name}, ${city.country}",
            style = TextStyle(fontSize = 17.sp, color = Color.White),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 20.dp, top = 20.dp)
        )

        // Weather Info Section
        if (weather != null) {
            // Success state - show weather data
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 20.dp, bottom = 20.dp)
            ) {
                Text(
                    text = "${weather.temp}°",
                    style = TextStyle(fontSize = 64.sp, color = Color.White, fontWeight = FontWeight.Normal)
                )
                Text(
                    text = "H:${weather.high}°  L:${weather.low}°",
                    style = TextStyle(fontSize = 13.sp, color = Color.White.copy(alpha = 0.6f))
                )
            }

            // Weather Illustration
            Image(
                painter = painterResource(id = weather.iconRes),
                contentDescription = null,
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 10.dp, y = (-20).dp)
            )

            // Condition Text
            Text(
                text = weather.condition,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 20.dp),
                style = TextStyle(fontSize = 13.sp, color = Color.White)
            )
        } else if (isLoading) {
            // Loading state
            Box(
                modifier = Modifier
                    .matchParentSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        } else {
            // No weather data yet - show placeholder
            Column(
                modifier = Modifier
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Show placeholder temperature
                Text(
                    text = "--°",
                    style = TextStyle(fontSize = 48.sp, color = Color.White.copy(alpha = 0.5f))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "H:--°  L:--°",
                    style = TextStyle(fontSize = 13.sp, color = Color.White.copy(alpha = 0.4f))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Loading weather...",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                )
            }
        }


    }
}


@Composable
private fun AddCityHeader(
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Back Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_left),
                contentDescription = "Go back",
                tint = Color.White,
            )
        }
        Text("Weather", style = MaterialTheme.typography.titleLarge, color = Color.White)

        // More Button
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_more),
                contentDescription = "More options",
                tint = Color.White
            )
        }
    }
}


@Composable
private fun StyledSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp),
        textStyle = TextStyle(color = Color.White, fontSize = 17.sp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
            }
        ),
        cursorBrush = SolidColor(Color.White),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .background(
                        color = Color(0xFF1C1B33).copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .border(
                        width = 0.5.dp,
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFFEBEBF5).copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Box {
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
private fun GlassSearchSuggestions(
    suggestions: List<CitySearchResult>,
    isLoading: Boolean,
    onSuggestionClick: (CitySearchResult) -> Unit,
    modifier: Modifier = Modifier
) {
    if (suggestions.isEmpty() && !isLoading) return

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 300.dp)
            .shadow(20.dp, RoundedCornerShape(22.dp)),
        color = Color(0xFF48319D).copy(alpha = 0.4f), // Matching the Detail Card alpha
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(32.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                itemsIndexed(suggestions, key = { _, it -> it.id }) { index, suggestion ->
                    GlassSuggestionItem(
                        suggestion = suggestion,
                        onClick = { onSuggestionClick(suggestion) }
                    )

                    if (index < suggestions.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = Color.White.copy(alpha = 0.1f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GlassSuggestionItem(
    suggestion: CitySearchResult,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            color = Color.White.copy(alpha = 0.1f),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_location),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // City details
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = suggestion.name,
                style = AppTypography.bodyBold,
                color = Color.White
            )

            Text(
                text = "${suggestion.country}${suggestion.admin1?.let { ", $it" } ?: ""}",
                style = AppTypography.caption2,
                color = Color.White.copy(alpha = 0.6f)
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_plus),
            contentDescription = "Add city",
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(22.dp)
        )
    }
}
