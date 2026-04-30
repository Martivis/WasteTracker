package com.example.wastetracker.data.repository

import com.example.wastetracker.data.model.FinanceOperation
import com.example.wastetracker.data.remote.api.OperationsApiService
import com.example.wastetracker.data.remote.dto.OperationRequest
import com.example.wastetracker.data.remote.dto.OperationResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NetworkFinanceRepository(private val api: OperationsApiService) : FinanceRepository {
    private val _operations = MutableStateFlow<List<FinanceOperation>>(emptyList())
    private val _balance = MutableStateFlow(0.0)

    override fun getAllOperations(): Flow<List<FinanceOperation>> {
        return _operations.asStateFlow()
    }

    override fun getBalance(): Flow<Double> {
        return _balance.asStateFlow()
    }

    override suspend fun refreshData(): Result<Unit> {
        return try {
            val opsRes = api.getAllOperations()
            val balRes = api.getBalance()
            
            if (opsRes.isSuccessful && opsRes.body() != null && balRes.isSuccessful && balRes.body() != null) {
                _operations.value = opsRes.body()!!.map { it.toDomain() }
                _balance.value = balRes.body()!!.balance
                Result.success(Unit)
            } else {
                val error = opsRes.errorBody()?.string() ?: balRes.errorBody()?.string() ?: "Failed to refresh data"
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getOperationById(id: String): FinanceOperation? {
        return _operations.value.find { it.id == id }
    }

    override suspend fun addOperation(operation: FinanceOperation) {
        try {
            val response = api.createOperation(operation.toRequest())
            if (response.isSuccessful) {
                refreshData()
            }
        } catch (e: Exception) {
        }
    }

    override suspend fun updateOperation(operation: FinanceOperation) {
        try {
            val response = api.updateOperation(operation.id, operation.toRequest())
            if (response.isSuccessful) {
                refreshData()
            }
        } catch (e: Exception) {
        }
    }

    override suspend fun deleteOperation(id: String) {
        try {
            val response = api.deleteOperation(id)
            if (response.isSuccessful) {
                refreshData()
            }
        } catch (e: Exception) {
        }
    }

    private fun OperationResponse.toDomain(): FinanceOperation {
        return FinanceOperation(
            id = id,
            type = type,
            amount = amount,
            category = category,
            date = LocalDate.parse(date, DateTimeFormatter.ISO_DATE),
            description = description
        )
    }

    private fun FinanceOperation.toRequest(): OperationRequest {
        return OperationRequest(
            type = type,
            amount = amount,
            category = category,
            date = date.format(DateTimeFormatter.ISO_DATE),
            description = description
        )
    }
}
