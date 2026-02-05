package com.amos_tech_code.weatherforecast.core.network

import com.amos_tech_code.weatherforecast.data.remote.dto.OpenMeteoErrorResponseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException

private val connectivityObserver: ConnectivityObserver by inject(ConnectivityObserver::class.java)

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): ApiResult<T> {

    // Check network connectivity before calling API
    if (!connectivityObserver.isConnected) {
        return ApiResult.Failure(
            ApiError.NetworkError(IOException("No internet connection. Please check your network and try again."))
        )
    }

    // Make the API call
    return try {
        val response = withContext(Dispatchers.IO) { apiCall() }
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                ApiResult.Success(body)
            } else {
                ApiResult.Failure(ApiError.UnknownError(Throwable("Empty server response. Please try again later.")))
            }
        } else {
            val errorMessage = parseErrorBody(response)
            ApiResult.Failure(
                ApiError.HttpError(
                    statusCode = response.code(),
                    reason = errorMessage
                )
            )
        }
    } catch (e: IOException) {
        val message = when (e) {
            is UnknownHostException -> "No internet connection. Please check your network."
            is SocketTimeoutException -> "Connection timed out. Please try again."
            is ConnectException -> "Unable to connect to the server. Please try again."
            is SSLHandshakeException, is SSLException -> "Secure connection failed. Please try again."
            else -> "Temporary network issue. Please try again later."
        }
        ApiResult.Failure(ApiError.NetworkError(IOException(message)))
    } catch (e: Exception) {
        ApiResult.Failure(ApiError.UnknownError(e))
    }

}

private fun <T> parseErrorBody(response: Response<T>): String {
    return try {
        val errorBody = response.errorBody()?.string()
        if (!errorBody.isNullOrBlank()) {
            val errorResponse = Json { ignoreUnknownKeys = true }
                .decodeFromString(OpenMeteoErrorResponseDto.serializer(), errorBody)
            errorResponse.reason
        } else {
            response.message()
        }
    } catch (_: SerializationException) {
        response.message()
    } catch (_: Exception) {
        response.message()
    }
}