package com.example.wastetracker.data.remote.dto

import com.example.wastetracker.data.model.OperationType

data class OperationRequest(
    val type: OperationType,
    val amount: Double,
    val category: String,
    val date: String, // ISO-8601 string
    val description: String? = null
)

data class OperationResponse(
    val id: String,
    val type: OperationType,
    val amount: Double,
    val category: String,
    val date: String,
    val description: String? = null
)

data class BalanceResponse(
    val balance: Double
)
