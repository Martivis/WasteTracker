package com.example.wastetracker.data.local.dao

import androidx.room.*
import com.example.wastetracker.data.local.entity.OperationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OperationDao {
    @Query("SELECT * FROM operations WHERE isDeleted = 0 ORDER BY date DESC")
    fun getAllOperations(): Flow<List<OperationEntity>>

    @Query("SELECT * FROM operations WHERE id = :id AND isDeleted = 0")
    suspend fun getOperationById(id: String): OperationEntity?

    @Query("SELECT * FROM operations WHERE isSynced = 0")
    suspend fun getUnsyncedOperations(): List<OperationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperations(operations: List<OperationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(operation: OperationEntity)

    @Update
    suspend fun updateOperation(operation: OperationEntity)

    @Query("DELETE FROM operations WHERE id = :id")
    suspend fun deleteOperationById(id: String)

    @Query("DELETE FROM operations")
    suspend fun deleteAllOperations()
}
