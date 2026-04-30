package com.example.wastetracker.data.repository

import com.example.wastetracker.data.model.FinanceOperation
import com.example.wastetracker.data.model.OperationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class InMemoryFinanceRepository : FinanceRepository {
    private val operations = MutableStateFlow<List<FinanceOperation>>(emptyList())

    override fun getAllOperations(): Flow<List<FinanceOperation>> {
        return operations
    }

    override fun getOperationById(id: String): FinanceOperation? {
        return operations.value.find { it.id == id }
    }

    override suspend fun addOperation(operation: FinanceOperation) {
        operations.update { list -> list + operation }
    }

    override suspend fun updateOperation(operation: FinanceOperation) {
        operations.update { list ->
            list.map { if (it.id == operation.id) operation else it }
        }
    }

    override suspend fun deleteOperation(id: String) {
        operations.update { list ->
            list.filterNot { it.id == id }
        }
    }

    override fun getBalance(): Flow<Double> {
        return operations.map { list ->
            list.sumOf { if (it.type == OperationType.INCOME) it.amount else -it.amount }
        }
    }

    override suspend fun refreshData(): Result<Unit> {
        return Result.success(Unit)
    }
}
