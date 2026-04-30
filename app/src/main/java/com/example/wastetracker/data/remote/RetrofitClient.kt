package com.example.wastetracker.data.remote

import com.example.wastetracker.data.remote.api.AuthApiService
import com.example.wastetracker.data.remote.api.AuthInterceptor
import com.example.wastetracker.data.remote.api.OperationsApiService
import com.example.wastetracker.util.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient(tokenManager: TokenManager) {
    private val BASE_URL = "https://gateway-staging.superglue.games/android/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(tokenManager))
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authService: AuthApiService = retrofit.create(AuthApiService::class.java)
    val operationsService: OperationsApiService = retrofit.create(OperationsApiService::class.java)
}
