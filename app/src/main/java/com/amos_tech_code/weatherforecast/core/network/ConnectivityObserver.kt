package com.amos_tech_code.weatherforecast.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService

/**
 * Connectivity Observer implementation using the ConnectivityManager API.
 */
class ConnectivityObserver(
    context: Context
) {
    private val connectivityManager = context.getSystemService<ConnectivityManager>()!!

    val isConnected: Boolean
        get() {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(network) ?: return false

            return capabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_VALIDATED
            )
        }
}