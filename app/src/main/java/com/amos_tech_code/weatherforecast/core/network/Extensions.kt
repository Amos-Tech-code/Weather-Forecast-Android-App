package com.amos_tech_code.weatherforecast.core.network

/**
 * Helper function to extract error message from ApiError
 * @return String representation of the error message
 * @see ApiError
 */

fun ApiError.extractApiErrorMessage(): String {
    return when (this) {
        is ApiError.NetworkError -> {
            "Network error: ${this.exception.message ?: "Please check your internet connection"}"
        }

        is ApiError.HttpError -> {
            "Error: ${this.reason}"
        }

        is ApiError.UnknownError -> {
            "An unexpected error occurred: ${this.throwable.message ?: "Please try again"}"
        }
    }
}