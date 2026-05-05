package com.example.wastetracker.data.repository

import com.example.wastetracker.data.remote.api.AuthApiService
import com.example.wastetracker.data.remote.dto.LoginRequest
import com.example.wastetracker.data.remote.dto.RegisterRequest
import com.example.wastetracker.data.remote.dto.TokenResponse
import com.example.wastetracker.util.TokenManager
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApiService,
    private val tokenManager: TokenManager
) {
    suspend fun register(request: RegisterRequest): Result<TokenResponse> {
        return try {
            val response = api.register(request)
            if (response.isSuccessful && response.body() != null) {
                tokenManager.saveToken(response.body()!!.token)
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception("Registration failed: $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(request: LoginRequest): Result<TokenResponse> {
        return try {
            val response = api.login(request)
            if (response.isSuccessful && response.body() != null) {
                tokenManager.saveToken(response.body()!!.token)
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception("Login failed: $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        tokenManager.deleteToken()
    }

    fun isLoggedIn(): Boolean {
        return tokenManager.getToken() != null
    }
}
