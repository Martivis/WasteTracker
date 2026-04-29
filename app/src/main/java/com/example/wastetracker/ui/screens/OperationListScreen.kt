package com.example.wastetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wastetracker.data.model.FinanceOperation
import com.example.wastetracker.data.model.OperationType
import com.example.wastetracker.ui.viewmodel.FinanceViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperationListScreen(
    viewModel: FinanceViewModel,
    onAddOperation: () -> Unit,
    onOperationClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои Финансы") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddOperation) {
                Icon(Icons.Default.Add, contentDescription = "Add Operation")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            BalanceCard(uiState.balance)
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.operations) { operation ->
                    OperationCard(
                        operation = operation,
                        onClick = { onOperationClick(operation.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun BalanceCard(balance: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Текущий баланс",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = String.format(Locale.getDefault(), "%.2f ₽", balance),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = if (balance >= 0) Color(0xFF388E3C) else Color(0xFFD32F2F)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperationCard(operation: FinanceOperation, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = operation.category,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = operation.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = String.format(
                    Locale.getDefault(),
                    "%s %.2f ₽",
                    if (operation.type == OperationType.INCOME) "+" else "-",
                    operation.amount
                ),
                style = MaterialTheme.typography.titleLarge,
                color = if (operation.type == OperationType.INCOME) Color(0xFF388E3C) else Color(0xFFD32F2F)
            )
        }
    }
}
