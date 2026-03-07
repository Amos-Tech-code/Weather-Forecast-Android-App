package com.amos_tech_code.weatherforecast.ui.feature.home


sealed class HomeEvent {
    data class ShowErrorMessage(val message: String) : HomeEvent()

    object NavigateToAddCity : HomeEvent()


}