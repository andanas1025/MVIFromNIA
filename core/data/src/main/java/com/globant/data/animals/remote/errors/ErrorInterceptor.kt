package com.globant.data.animals.remote.errors

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            val response = chain.proceed(chain.request())

            // 🚀 FIX: Access the public .code property directly (no internal field)
            if (!response.isSuccessful) {
                throw when (response.code()) {
                    404 -> IOException("NOT_FOUND")
                    503 -> IOException("MAINTENANCE")
                    else -> IOException("HTTP_ERROR_${response.code()}")
                }
            }
            return response
        } catch (e: Exception) {
            throw when (e) {
                is UnknownHostException -> IOException("NO_INTERNET")
                is SocketTimeoutException -> IOException("TIMEOUT")
                else -> e
            }
        }
    }
}