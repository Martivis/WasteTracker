package com.example.wastetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wastetracker.ui.screens.*
import com.example.wastetracker.ui.viewmodel.AuthViewModel
import com.example.wastetracker.ui.viewmodel.FinanceViewModel
import com.example.wastetracker.util.TokenManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authViewModel: AuthViewModel = hiltViewModel()
                    val financeViewModel: FinanceViewModel = hiltViewModel()
                    
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
