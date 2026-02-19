package com.amos_tech_code.weatherforecast.ui.feature.saved_cities

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.amos_tech_code.weatherforecast.R
import com.amos_tech_code.weatherforecast.core.util.ObserveAsEvents
import com.amos_tech_code.weatherforecast.domain.model.CityWeather
import com.amos_tech_code.weatherforecast.ui.components.EmptySavedCitiesView
import com.amos_tech_code.weatherforecast.ui.components.GlassIconButton
import com.amos_tech_code.weatherforecast.ui.components.LoadingView
import com.amos_tech_code.weatherforecast.ui.navigation.AddCityRoute
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedCitiesScreen(
    navController: NavController,
    viewModel: SavedCitiesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.event) { event ->
        when(event) {
            is SavedCitiesScreenEvent.ShowErrorMessage -> {
                scope.launch {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
            is SavedCitiesScreenEvent.CityRemoved -> {
                scope.launch {
                    snackbarHostState.showSnackbar("City removed successfully")
                }
            }
            is SavedCitiesScreenEvent.AddNewCity -> {
                navController.navigate(AddCityRoute)
            }

            is SavedCitiesScreenEvent.AddToSavedState -> {
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    "selected_city",
                    event.city
                )
                navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            SavedCitiesTopBar(
                onBackClick = { navController.popBackStack() },
                onAdd = { viewModel.onAddCityClick() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF2E335A), Color(0xFF1C1B33))))
                .padding(innerPadding)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {

                if (uiState.isLoading) {
                    LoadingView()
                } else {
                    if (uiState.cities.isEmpty()) {
                        EmptySavedCitiesView()
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            contentPadding = PaddingValues(bottom = 120.dp) // Room for BottomBar
                        ) {
                            items(uiState.cities, key = { it.city.id }) { city ->
                                SavedCityWeatherCard(
                                    cityWeather = city,
                                    onClick = { viewModel.onAddToSavedState(city) },
                                    onRemoveClick = { viewModel.removeCity(city.city.id) },
                                )
                            }
                        }
                    }
                }
            }

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SavedCitiesTopBar(
    onBackClick: () -> Unit,
    onAdd: () -> Unit
) {
   CenterAlignedTopAppBar(
       title = {
           Text(
               text = "Saved Cities",
               style = MaterialTheme.typography.titleLarge.copy(
                   color = Color.White,
                   fontWeight = FontWeight.SemiBold
               )
           )
       },
       navigationIcon = {
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
       },
       actions = {
           GlassIconButton(
               onClick = onAdd,
               icon = Icons.Default.Add,
               contentDescription = "Add City",
               modifier = Modifier.size(40.dp)
           )
       },
       colors = TopAppBarDefaults.topAppBarColors(
           containerColor = Color.Transparent,
       )
   )
}

@Composable
private fun SavedCityWeatherCard(
    modifier: Modifier = Modifier,
    cityWeather: CityWeather,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit,
) {
    Box(
        modifier = modifier
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
        )

        // City name (always shown)
        Text(
            text = "${cityWeather.city.name}, ${cityWeather.city.country}",
            style = TextStyle(fontSize = 17.sp, color = Color.White),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 20.dp, top = 20.dp)
        )

        // Weather Info Section
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 20.dp)
        ) {
            Text(
                text = "${cityWeather.temp}°",
                style = TextStyle(
                    fontSize = 64.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Normal
                )
            )
            Text(
                text = "H:${cityWeather.high}°  L:${cityWeather.low}°",
                style = TextStyle(fontSize = 13.sp, color = Color.White.copy(alpha = 0.6f))
            )
        }

        // Condition Text
        Text(
            text = cityWeather.condition,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp),
            style = TextStyle(fontSize = 13.sp, color = Color.White)
        )

        // Action Buttons
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Weather Illustration
            Image(
                painter = painterResource(id = cityWeather.iconRes),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    //.offset(x = 10.dp, y = (-20).dp)
            )
            // Remove Button
            GlassIconButton(
                onClick = onRemoveClick,
                icon = Icons.Default.Close,
                contentDescription = "Remove",
                modifier = Modifier.size(32.dp),
                iconTint = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}