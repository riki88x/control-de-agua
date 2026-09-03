package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Subscriber
import com.example.ui.theme.OceanBluePrimary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReceiptDialog(
    subscribers: List<Subscriber>,
    onDismiss: () -> Unit,
    onSaveReceipt: (
        subscriberId: Long,
        period: String,
        prevReading: Double,
        currReading: Double,
        pricePerM3: Double,
        fixedFee: Double,
        sewerFee: Double,
        dueDate: Long,
        notes: String
    ) -> Unit
) {
    var selectedSubscriber by remember { mutableStateOf(subscribers.firstOrNull()) }
    var expandedSubscriberMenu by remember { mutableStateOf(false) }

    val currentMonthFormat = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))
    var periodText by remember { mutableStateOf(currentMonthFormat.format(Date()).replaceFirstChar { it.uppercase() }) }

    var prevReadingText by remember { mutableStateOf("100.0") }
    var currReadingText by remember { mutableStateOf("125.0") }
    var pricePerM3Text by remember { mutableStateOf("12.50") }
    var fixedFeeText by remember { mutableStateOf("65.00") }
    var sewerFeeText by remember { mutableStateOf("35.00") }
    var daysDueChoice by remember { mutableStateOf(15) }
    var notesText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Calculated fields
    val prevVal = prevReadingText.toDoubleOrNull() ?: 0.0
    val currVal = currReadingText.toDoubleOrNull() ?: 0.0
    val consumption = (currVal - prevVal).coerceAtLeast(0.0)
    val priceVal = pricePerM3Text.toDoubleOrNull() ?: 0.0
    val fixedVal = fixedFeeText.toDoubleOrNull() ?: 0.0
    val sewerVal = sewerFeeText.toDoubleOrNull() ?: 0.0
    val estimatedTotal = (consumption * priceVal) + fixedVal + sewerVal

    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, daysDueChoice)
    val calculatedDueDate = cal.timeInMillis
    val dueDateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(calculatedDueDate))

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("add_receipt_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(OceanBluePrimary.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                tint = OceanBluePrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Nuevo Recibo de Agua",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Emisión y cálculo por lectura",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Subscriber selector
                Text(
                    text = "Cliente / Suscriptor",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                ExposedDropdownMenuBox(
                    expanded = expandedSubscriberMenu,
                    onExpandedChange = { expandedSubscriberMenu = !expandedSubscriberMenu }
                ) {
                    OutlinedTextField(
                        value = selectedSubscriber?.let { "${it.name} (${it.accountNumber})" } ?: "Seleccionar Cliente",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubscriberMenu) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedSubscriberMenu,
                        onDismissRequest = { expandedSubscriberMenu = false }
                    ) {
                        subscribers.forEach { sub ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(sub.name, fontWeight = FontWeight.Medium)
                                        Text(
                                            "Cta: ${sub.accountNumber} • Medidor: ${sub.meterNumber}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                },
                                onClick = {
                                    selectedSubscriber = sub
                                    expandedSubscriberMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Period
                OutlinedTextField(
                    value = periodText,
                    onValueChange = { periodText = it },
                    label = { Text("Período de Facturación") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Readings row
                Text(
                    text = "Lecturas del Medidor (m³)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = prevReadingText,
                        onValueChange = { prevReadingText = it },
                        label = { Text("Lect. Anterior") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = currReadingText,
                        onValueChange = { currReadingText = it },
                        label = { Text("Lect. Actual") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Consumption banner
                Surface(
                    color = OceanBluePrimary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💧 Consumo del período:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OceanBluePrimary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = String.format(Locale.US, "%.1f m³", consumption),
                            style = MaterialTheme.typography.titleMedium,
                            color = OceanBluePrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Rates: Price per m3, Fixed Fee, Sewer Fee
                Text(
                    text = "Tarifas y Cuotas Base",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = pricePerM3Text,
                        onValueChange = { pricePerM3Text = it },
                        label = { Text("$/m³") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = fixedFeeText,
                        onValueChange = { fixedFeeText = it },
                        label = { Text("Cargo Fijo") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1.1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = sewerFeeText,
                        onValueChange = { sewerFeeText = it },
                        label = { Text("Saneam.") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1.1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Due date selector
                Text(
                    text = "Plazo de Pago (Vencimiento: $dueDateStr)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(7, 15, 30).forEach { days ->
                        val isSelected = daysDueChoice == days
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) OceanBluePrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { daysDueChoice = days }
                        ) {
                            Text(
                                text = "$days Días",
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = if (isSelected) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Notes
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Observaciones (Opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Total calculated banner
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total a Facturar:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = String.format(Locale.US, "$%.2f MXN", estimatedTotal),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = OceanBluePrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            if (selectedSubscriber == null) {
                                errorMessage = "Por favor seleccione un cliente"
                                return@Button
                            }
                            if (currVal < prevVal) {
                                errorMessage = "La lectura actual no puede ser menor a la anterior"
                                return@Button
                            }
                            if (estimatedTotal <= 0) {
                                errorMessage = "El total debe ser mayor a 0"
                                return@Button
                            }

                            onSaveReceipt(
                                selectedSubscriber!!.id,
                                periodText.trim(),
                                prevVal,
                                currVal,
                                priceVal,
                                fixedVal,
                                sewerVal,
                                calculatedDueDate,
                                notesText.trim()
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OceanBluePrimary),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("save_receipt_button")
                    ) {
                        Text("Emitir Recibo", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
