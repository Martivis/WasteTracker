package com.example.wastetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wastetracker.data.remote.dto.LoginRequest
import com.example.wastetracker.data.remote.dto.RegisterRequest
import com.example.wastetracker.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val result = repository.register(request)
            if (result.isSuccess) {
                _uiState.value = AuthUiState(isSuccess = true)
            } else {
                _uiState.value = AuthUiState(error = result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val result = repository.login(request)
            if (result.isSuccess) {
                _uiState.value = AuthUiState(isSuccess = true)
            } else {
                _uiState.value = AuthUiState(error = result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun isLoggedIn() = repository.isLoggedIn()
}
