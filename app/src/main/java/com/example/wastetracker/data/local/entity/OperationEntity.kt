package com.example.wastetracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.wastetracker.data.model.FinanceOperation
import com.example.wastetracker.data.model.OperationType
import com.example.wastetracker.data.remote.dto.OperationResponse
import java.time.LocalDate

@Entity(tableName = "operations")
data class OperationEntity(
    @PrimaryKey
    val id: String,
    val type: OperationType,
    val amount: Double,
    val category: String,
    val date: LocalDate,
    val description: String?,
    val isSynced: Boolean = true,
    val isDeleted: Boolean = false
)

fun OperationEntity.toDomain() = FinanceOperation(
    id = id,
    type = type,
    amount = amount,
    category = category,
    date = date,
    description = description
)

fun FinanceOperation.toEntity(isSynced: Boolean = true, isDeleted: Boolean = false) = OperationEntity(
    id = id,
    type = type,
    amount = amount,
    category = category,
    date = date,
    description = description,
    isSynced = isSynced,
    isDeleted = isDeleted
)

fun OperationResponse.toEntity() = OperationEntity(
    id = id,
    type = type,
    amount = amount,
    category = category,
    date = LocalDate.parse(date),
    description = description
)

fun OperationResponse.toDomain() = FinanceOperation(
    id = id,
    type = type,
    amount = amount,
    category = category,
    date = LocalDate.parse(date),
    description = description
)
