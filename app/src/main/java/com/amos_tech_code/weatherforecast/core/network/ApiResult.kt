package com.amos_tech_code.weatherforecast.core.network

import java.io.IOException

/**
 * A sealed class representing the result of an API call.

 */
sealed class ApiResult<out T> {

    data class Success<out T>(val data: T): ApiResult<T>()

    data class Failure(val error: ApiError): ApiResult<Nothing>()
}


/**
 * Sealed class representing different types of errors that can occur during API calls.
 * @see ApiResult
 */
sealed class ApiError {

    data class HttpError(val statusCode: Int, val reason: String): ApiError()

    data class NetworkError(val exception: IOException): ApiError()

    data class UnknownError(val throwable: Throwable): ApiError()

}