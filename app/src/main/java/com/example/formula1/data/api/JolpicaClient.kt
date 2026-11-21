package com.example.formula1.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/** Pravi jedan Retrofit objekat za celu aplikaciju. */
object JolpicaClient {

    private const val BASE_URL = "https://api.jolpi.ca/ergast/"
    private const val CONNECT_TIMEOUT_SECONDS = 15L
    private const val READ_TIMEOUT_SECONDS = 20L

    val api: JolpicaApi by lazy {
        val http = OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(RetryWhenTooManyRequests())
            .build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(http)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JolpicaApi::class.java)
    }
}

/**
 * API ogranicava broj poziva (HTTP 429). Ako se to desi (ili server privremeno javi 5xx),
 * sacekamo (koliko API trazi preko Retry-After, a najmanje malo duze sa svakim pokusajem)
 * i pokusamo ponovo.
 */
class RetryWhenTooManyRequests : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var response = chain.proceed(chain.request())
        var attempt = 0
        while (shouldRetry(response.code) && attempt < MAX_RETRIES) {
            val asked = response.header("Retry-After")?.toLongOrNull()?.times(1000) ?: 0L
            response.close()
            attempt++
            Thread.sleep(maxOf(asked, WAIT_MILLIS * attempt).coerceAtMost(MAX_WAIT_MILLIS))
            response = chain.proceed(chain.request())
        }
        return response
    }

    private fun shouldRetry(code: Int) = code == TOO_MANY_REQUESTS || code in 500..599

    private companion object {
        const val TOO_MANY_REQUESTS = 429
        const val MAX_RETRIES = 4
        const val WAIT_MILLIS = 1500L
        const val MAX_WAIT_MILLIS = 8000L
    }
}
