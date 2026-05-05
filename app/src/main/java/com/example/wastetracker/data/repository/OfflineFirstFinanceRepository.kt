package com.example.wastetracker.data.repository

import com.example.wastetracker.data.local.dao.OperationDao
import com.example.wastetracker.data.local.entity.OperationEntity
import com.example.wastetracker.data.local.entity.toDomain
import com.example.wastetracker.data.local.entity.toEntity
import com.example.wastetracker.data.model.FinanceOperation
import com.example.wastetracker.data.model.OperationType
import com.example.wastetracker.data.remote.api.OperationsApiService
import com.example.wastetracker.data.remote.dto.OperationRequest
import com.example.wastetracker.data.remote.dto.OperationResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineFirstFinanceRepository @Inject constructor(
    private val dao: OperationDao,
    private val api: OperationsApiService
) : FinanceRepository {

    override fun getAllOperations(): Flow<List<FinanceOperation>> {
        return dao.getAllOperations().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getOperationById(id: String): FinanceOperation? {
        return dao.getOperationById(id)?.toDomain()
    }

    override suspend fun addOperation(operation: FinanceOperation) {
        // Save locally first with isSynced = false
        dao.insertOperation(operation.toEntity(isSynced = false))
        syncPending()
    }

    override suspend fun updateOperation(operation: FinanceOperation) {
        dao.insertOperation(operation.toEntity(isSynced = false))
        syncPending()
    }

    override suspend fun deleteOperation(id: String) {
        val operation = dao.getOperationById(id)
        if (operation != null) {
            dao.insertOperation(operation.copy(isDeleted = true, isSynced = false))
        }
        syncPending()
    }

    private suspend fun syncPending() {
        val pending = dao.getUnsyncedOperations()
        for (entity in pending) {
            try {
                if (entity.isDeleted) {
                    val response = api.deleteOperation(entity.id)
                    if (response.isSuccessful) {
                        dao.deleteOperationById(entity.id)
                    }
                } else {
                    val request = OperationRequest(
                        id = entity.id,
                        type = entity.type,
                        amount = entity.amount,
                        category = entity.category,
                        date = entity.date.toString(),
                        description = entity.description
                    )
                    
                    val response = api.createOperation(request)
                    if (response.isSuccessful && response.body() != null) {
                        val serverResponse = response.body()!!

                        if (serverResponse.id != entity.id) {
                            dao.deleteOperationById(entity.id)
                        }
                        dao.insertOperation(serverResponse.toEntity())
                    } else if (response.code() == 409 || response.code() == 400) {
                        val updateResponse = api.updateOperation(entity.id, request)
                        if (updateResponse.isSuccessful) {
                            dao.insertOperation(entity.copy(isSynced = true))
                        }
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    override fun getBalance(): Flow<Double> {
        return dao.getAllOperations().map { list ->
            list.sumOf { if (it.type == OperationType.INCOME) it.amount else -it.amount }
        }
    }

    override suspend fun refreshData(): Result<Unit> {
        syncPending()
        
        return try {
            val response = api.getAllOperations()
            if (response.isSuccessful && response.body() != null) {
                val remoteEntities = response.body()!!.map { it.toEntity() }

                val stillPending = dao.getUnsyncedOperations()
                
                dao.deleteAllOperations()
                dao.insertOperations(remoteEntities)
                dao.insertOperations(stillPending)
                
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to fetch data"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
