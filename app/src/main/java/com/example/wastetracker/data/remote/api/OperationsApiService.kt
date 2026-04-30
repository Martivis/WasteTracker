package com.example.wastetracker.data.remote.api

import com.example.wastetracker.data.remote.dto.BalanceResponse
import com.example.wastetracker.data.remote.dto.OperationRequest
import com.example.wastetracker.data.remote.dto.OperationResponse
import retrofit2.Response
import retrofit2.http.*

interface OperationsApiService {
    @GET("api/v1/operations")
    suspend fun getAllOperations(): Response<List<OperationResponse>>

    @GET("api/v1/operations/{id}")
    suspend fun getOperationById(@Path("id") id: String): Response<OperationResponse>

    @POST("api/v1/operations")
    suspend fun createOperation(@Body request: OperationRequest): Response<OperationResponse>

    @PUT("api/v1/operations/{id}")
    suspend fun updateOperation(@Path("id") id: String, @Body request: OperationRequest): Response<OperationResponse>

    @DELETE("api/v1/operations/{id}")
    suspend fun deleteOperation(@Path("id") id: String): Response<Unit>

    @GET("api/v1/operations/balance")
    suspend fun getBalance(): Response<BalanceResponse>
}
