package com.example.wastetracker.data.repository

import com.example.wastetracker.data.model.FinanceOperation
import kotlinx.coroutines.flow.Flow

interface FinanceRepository {
    fun getAllOperations(): Flow<List<FinanceOperation>>
    fun getOperationById(id: String): FinanceOperation?
    suspend fun addOperation(operation: FinanceOperation)
    suspend fun updateOperation(operation: FinanceOperation)
    suspend fun deleteOperation(id: String)
}
