package com.example.wastetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.wastetracker.data.model.FinanceOperation
import com.example.wastetracker.data.model.OperationType
import com.example.wastetracker.ui.viewmodel.FinanceViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditOperationScreen(
    viewModel: FinanceViewModel,
    operationId: String? = null,
    onNavigateBack: () -> Unit
) {
    val operation by viewModel.currentOperation.collectAsState()

    LaunchedEffect(operationId) {
        if (operationId != null) {
            viewModel.fetchOperation(operationId)
        } else {
            viewModel.clearCurrentOperation()
        }
    }

    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(OperationType.EXPENSE) }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var description by remember { mutableStateOf("") }

    LaunchedEffect(operation) {
        operation?.let {
            amount = it.amount.toString()
            category = it.category
            type = it.type
            date = it.date
            description = it.description ?: ""
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (operationId == null) "Добавить операцию" else "Редактировать") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = type == OperationType.EXPENSE,
                    onClick = { type = OperationType.EXPENSE },
                    label = { Text("Расход") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = type == OperationType.INCOME,
                    onClick = { type = OperationType.INCOME },
                    label = { Text("Доход") },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Сумма") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Категория") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                onValueChange = { },
                label = { Text("Дата") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                }
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание (опционально)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val opAmount = amount.toDoubleOrNull() ?: 0.0
                    val newOperation = FinanceOperation(
                        id = operation?.id ?: java.util.UUID.randomUUID().toString(),
                        type = type,
                        amount = opAmount,
                        category = category,
                        date = date,
                        description = description.ifBlank { null }
                    )
                    if (operationId == null) {
                        viewModel.addOperation(newOperation)
                    } else {
                        viewModel.updateOperation(newOperation)
                    }
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = amount.isNotBlank() && category.isNotBlank()
            ) {
                Text("Сохранить")
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        date = java.time.Instant.ofEpochMilli(it)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
