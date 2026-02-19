package com.amos_tech_code.weatherforecast

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.amos_tech_code.weatherforecast.data.local.shared_prefs.WeatherAppPreferences
import com.amos_tech_code.weatherforecast.ui.navigation.AddCityRoute
import com.amos_tech_code.weatherforecast.ui.navigation.AppNavGraph
import com.amos_tech_code.weatherforecast.ui.navigation.HomeRoute
import com.amos_tech_code.weatherforecast.ui.theme.WeatherForecastTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val weatherAppPreferences by inject<WeatherAppPreferences>()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            WeatherForecastTheme {
                val startDestination = if (weatherAppPreferences.isLocationSet()) HomeRoute else AddCityRoute

                Scaffold(modifier = Modifier.fillMaxSize()) {

                    AppNavGraph(
                        startDestination = startDestination,
                        navController = rememberNavController(),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}