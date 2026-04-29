package com.example.wastetracker.data.model

import java.time.LocalDate
import java.util.UUID

enum class OperationType {
    INCOME, EXPENSE
}

data class FinanceOperation(
    val id: String = UUID.randomUUID().toString(),
    val type: OperationType,
    val amount: Double,
    val category: String,
    val date: LocalDate,
    val description: String? = null
)
