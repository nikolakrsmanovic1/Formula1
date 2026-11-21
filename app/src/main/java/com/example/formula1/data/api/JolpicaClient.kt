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
 * API ogranicava broj poziva u sekundi (HTTP 429). Ako se to desi, kratko sacekamo
 * i pokusamo ponovo, najvise dva puta.
 */
class RetryWhenTooManyRequests : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var response = chain.proceed(chain.request())
        var attempt = 0
        while (response.code == TOO_MANY_REQUESTS && attempt < MAX_RETRIES) {
            response.close()
            attempt++
            Thread.sleep(WAIT_MILLIS * attempt)
            response = chain.proceed(chain.request())
        }
        return response
    }

    private companion object {
        const val TOO_MANY_REQUESTS = 429
        const val MAX_RETRIES = 2
        const val WAIT_MILLIS = 1000L
    }
}
