package com.amos_tech_code.weatherforecast.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.amos_tech_code.weatherforecast.ui.feature.add_city.AddCityScreen
import com.amos_tech_code.weatherforecast.ui.feature.home.HomeScreen
import com.amos_tech_code.weatherforecast.ui.feature.saved_cities.SavedCitiesScreen

@Composable
fun AppNavGraph(
    startDestination: NavRoutes,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        composable<HomeRoute> {
            HomeScreen(navController)
        }

        composable<AddCityRoute> {
            AddCityScreen(navController)
        }

        composable<SavedCitiesRoute> {
            SavedCitiesScreen(navController)
        }

    }
}