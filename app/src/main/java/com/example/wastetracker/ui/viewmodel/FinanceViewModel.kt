package com.example.wastetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wastetracker.data.model.FinanceOperation
import com.example.wastetracker.data.repository.FinanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class FinanceUiState(
    val operations: List<FinanceOperation> = emptyList(),
    val balance: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class FinanceViewModel(private val repository: FinanceRepository) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<FinanceUiState> = combine(
        repository.getAllOperations(),
        repository.getBalance(),
        _isRefreshing,
        _error
    ) { operations, balance, isRefreshing, error ->
        FinanceUiState(
            operations = operations,
            balance = balance,
            isLoading = isRefreshing,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FinanceUiState(isLoading = true)
    )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val result = repository.refreshData()
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to refresh"
            } else {
                _error.value = null
            }
            _isRefreshing.value = false
        }
    }

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
    
    fun clearError() {
        _error.value = null
    }
}
