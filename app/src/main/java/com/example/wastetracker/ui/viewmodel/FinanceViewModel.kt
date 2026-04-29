package com.example.wastetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wastetracker.data.model.FinanceOperation
import com.example.wastetracker.data.model.OperationType
import com.example.wastetracker.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FinanceUiState(
    val operations: List<FinanceOperation> = emptyList(),
    val balance: Double = 0.0,
    val isLoading: Boolean = false
)

class FinanceViewModel(private val repository: FinanceRepository) : ViewModel() {

    val uiState: StateFlow<FinanceUiState> = repository.getAllOperations()
        .combine(MutableStateFlow(false)) { operations, isLoading ->
            val balance = operations.sumOf { 
                if (it.type == OperationType.INCOME) it.amount else -it.amount 
            }
            FinanceUiState(
                operations = operations,
                balance = balance,
                isLoading = isLoading
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FinanceUiState(isLoading = true)
        )

    fun addOperation(operation: FinanceOperation) {
        viewModelScope.launch {
            repository.addOperation(operation)
        }
    }

    fun updateOperation(operation: FinanceOperation) {
        viewModelScope.launch {
            repository.updateOperation(operation)
        }
    }

    fun deleteOperation(id: String) {
        viewModelScope.launch {
            repository.deleteOperation(id)
        }
    }
    
    fun getOperation(id: String): FinanceOperation? {
        return repository.getOperationById(id)
    }
}
