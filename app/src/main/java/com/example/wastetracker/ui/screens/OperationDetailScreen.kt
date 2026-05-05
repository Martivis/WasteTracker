package com.example.wastetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wastetracker.data.model.OperationType
import com.example.wastetracker.ui.viewmodel.FinanceViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperationDetailScreen(
    viewModel: FinanceViewModel,
    operationId: String,
    onEditClick: (String) -> Unit,
    onDeleteSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val operation by viewModel.currentOperation.collectAsState()

    LaunchedEffect(operationId) {
        viewModel.fetchOperation(operationId)
    }

    val op = operation
    if (op == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали операции") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEditClick(op.id) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { 
                        viewModel.deleteOperation(op.id)
                        onDeleteSuccess()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DetailItem(label = "Тип", value = if (op.type == OperationType.INCOME) "Доход" else "Расход")
            DetailItem(
                label = "Сумма", 
                value = String.format(Locale.getDefault(), "%.2f ₽", op.amount),
                valueColor = if (op.type == OperationType.INCOME) Color(0xFF388E3C) else Color(0xFFD32F2F)
            )
            DetailItem(label = "Категория", value = op.category)
            DetailItem(label = "Дата", value = op.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
            
            op.description?.let {
                DetailItem(label = "Описание", value = it)
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String, valueColor: Color = Color.Unspecified) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}
