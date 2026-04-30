package com.example.wastetracker.data.remote.api

import com.example.wastetracker.data.remote.dto.LoginRequest
import com.example.wastetracker.data.remote.dto.RegisterRequest
import com.example.wastetracker.data.remote.dto.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<TokenResponse>

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<TokenResponse>
}
