package com.example.wastetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wastetracker.data.remote.RetrofitClient
import com.example.wastetracker.data.repository.AuthRepository
import com.example.wastetracker.data.repository.NetworkFinanceRepository
import com.example.wastetracker.ui.screens.*
import com.example.wastetracker.ui.viewmodel.AuthViewModel
import com.example.wastetracker.ui.viewmodel.FinanceViewModel
import com.example.wastetracker.util.TokenManager

class MainActivity : ComponentActivity() {
    private lateinit var tokenManager: TokenManager
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var authRepository: AuthRepository
    private lateinit var financeRepository: NetworkFinanceRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        tokenManager = TokenManager(this)
        retrofitClient = RetrofitClient(tokenManager)
        authRepository = AuthRepository(retrofitClient.authService, tokenManager)
        financeRepository = NetworkFinanceRepository(retrofitClient.operationsService)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authViewModel: AuthViewModel = viewModel(
                        factory = ViewModelFactory(authRepository, financeRepository)
                    )
                    val financeViewModel: FinanceViewModel = viewModel(
                        factory = ViewModelFactory(authRepository, financeRepository)
                    )
                    
                    FinanceApp(authViewModel, financeViewModel)
                }
            }
        }
    }
}

@Composable
fun FinanceApp(authViewModel: AuthViewModel, financeViewModel: FinanceViewModel) {
    val navController = rememberNavController()
    val startDestination = if (authViewModel.isLoggedIn()) "list" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { 
                    financeViewModel.refresh()
                    navController.navigate("list") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    financeViewModel.refresh()
                    navController.navigate("list") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }
        composable("list") {
            OperationListScreen(
                viewModel = financeViewModel,
                onAddOperation = { navController.navigate("add_edit") },
                onOperationClick = { id -> navController.navigate("detail/$id") },
                onLogout = {
                    authViewModel.clearError() // reset state
                    // Simple logout: delete token and go to login
                    TokenManager(navController.context).deleteToken()
                    navController.navigate("login") {
                        popUpTo("list") { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            OperationDetailScreen(
                viewModel = financeViewModel,
                operationId = id,
                onEditClick = { navController.navigate("add_edit?id=$id") },
                onDeleteSuccess = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "add_edit?id={id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            AddEditOperationScreen(
                viewModel = financeViewModel,
                operationId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

// Simple unified factory
class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val financeRepository: NetworkFinanceRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(authRepository) as T
            modelClass.isAssignableFrom(FinanceViewModel::class.java) -> FinanceViewModel(financeRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
