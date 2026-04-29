package com.example.wastetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wastetracker.data.repository.InMemoryFinanceRepository
import com.example.wastetracker.ui.screens.AddEditOperationScreen
import com.example.wastetracker.ui.screens.OperationDetailScreen
import com.example.wastetracker.ui.screens.OperationListScreen
import com.example.wastetracker.ui.viewmodel.FinanceViewModel
import com.example.wastetracker.ui.viewmodel.FinanceViewModelFactory

class MainActivity : ComponentActivity() {
    private val repository = InMemoryFinanceRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: FinanceViewModel = viewModel(
                        factory = FinanceViewModelFactory(repository)
                    )
                    FinanceApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun FinanceApp(viewModel: FinanceViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            OperationListScreen(
                viewModel = viewModel,
                onAddOperation = { navController.navigate("add_edit") },
                onOperationClick = { id -> navController.navigate("detail/$id") }
            )
        }
        composable(
            route = "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            OperationDetailScreen(
                viewModel = viewModel,
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
                viewModel = viewModel,
                operationId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
