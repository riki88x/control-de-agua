package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReceiptWithSubscriber
import com.example.ui.theme.NaturalBorderOutline
import com.example.ui.theme.NaturalSurfaceVariant
import com.example.ui.theme.NaturalTextMuted
import com.example.ui.theme.NaturalTextPrimary
import com.example.ui.theme.NaturalTextSecondary
import com.example.ui.theme.OceanBlueContainer
import com.example.ui.theme.OceanBlueOnContainer
import com.example.ui.theme.OceanBluePrimary
import com.example.ui.theme.StatusOverdue
import com.example.ui.theme.StatusPaid
import com.example.ui.theme.StatusPending
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun PendingPaymentsScreen(
    debtorList: List<ReceiptWithSubscriber>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onPayReceipt: (ReceiptWithSubscriber) -> Unit,
    onSendReminder: (ReceiptWithSubscriber) -> Unit,
    modifier: Modifier = Modifier
) {
    val now = System.currentTimeMillis()
    val totalPendingAmount = debtorList.sumOf { it.receipt.pendingBalance }
    val overdueCount = debtorList.count { it.receipt.dueDate < now }
    val overdueAmount = debtorList.filter { it.receipt.dueDate < now }.sumOf { it.receipt.pendingBalance }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("pending_payments_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "Control de Cobranzas y Pagos Pendientes",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Cuentas por cobrar y gestión de cobranza de servicio de agua",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        // Summary Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = OceanBlueContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TOTAL PENDIENTE POR COBRAR",
                                style = MaterialTheme.typography.labelSmall,
                                color = OceanBlueOnContainer.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = String.format(Locale.US, "$%.2f MXN", totalPendingAmount),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = OceanBlueOnContainer
                            )
                        }
                        Surface(
                            color = OceanBluePrimary,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "${debtorList.size} recibos",
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = androidx.compose.ui.graphics.Color.White
                            )
                        }
                    }

                    if (overdueCount > 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = StatusOverdue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "$overdueCount recibos vencidos con mora: $${String.format(Locale.US, "%.2f", overdueAmount)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusOverdue
                                )
                            }
                        }
                    }
                }
            }
        }

        // Search bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("debtor_search_input"),
                placeholder = { Text("Buscar deudor por nombre, cuenta o medidor...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = OceanBluePrimary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
        }

        // Empty state or list
        if (debtorList.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎉", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Sin Cobros Pendientes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "No se encontraron recibos pendientes de pago.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        } else {
            items(debtorList, key = { "debtor_${it.receipt.id}" }) { item ->
                val r = item.receipt
                val sub = item.subscriber
                val isOverdue = r.dueDate < now
                val daysDiff = TimeUnit.MILLISECONDS.toDays(Math.abs(now - r.dueDate)).toInt()

                val initials = sub.name
                    .split(" ")
                    .filter { it.isNotBlank() }
                    .take(2)
                    .mapNotNull { it.firstOrNull()?.uppercase() }
                    .joinToString("")
                    .ifEmpty { "AG" }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("debtor_card_${r.id}"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, NaturalBorderOutline.copy(alpha = 0.8f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(NaturalSurfaceVariant, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = initials,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = NaturalTextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = sub.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = NaturalTextPrimary
                                )
                                Text(
                                    text = "Cuenta: ${sub.accountNumber} • Medidor: ${sub.meterNumber}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NaturalTextSecondary
                                )
                                if (sub.address.isNotBlank()) {
                                    Text(
                                        text = sub.address,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NaturalTextMuted,
                                        maxLines = 1
                                    )
                                }
                            }

                            // Pending balance tag
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = String.format(Locale.US, "$%.2f", r.pendingBalance),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOverdue) StatusOverdue else StatusPending
                                )
                                Text(
                                    text = if (isOverdue) "Vencido" else "Por Vencer",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOverdue) StatusOverdue else StatusPending
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Due status pill
                        Surface(
                            color = if (isOverdue) StatusOverdue.copy(alpha = 0.1f) else StatusPending.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isOverdue) Icons.Default.ErrorOutline else Icons.Default.HourglassEmpty,
                                    contentDescription = null,
                                    tint = if (isOverdue) StatusOverdue else StatusPending,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isOverdue) {
                                        "Venció el ${dateFormat.format(Date(r.dueDate))} (hace $daysDiff días de mora)"
                                    } else {
                                        "Vence el ${dateFormat.format(Date(r.dueDate))} (en $daysDiff días)"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isOverdue) StatusOverdue else StatusPending
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action buttons: Cobrar and Recordar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { onPayReceipt(item) },
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(containerColor = StatusPaid),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("cobrar_button_${r.id}")
                            ) {
                                Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Cobrar", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { onSendReminder(item) },
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, NaturalBorderOutline),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("recordar_button_${r.id}")
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, tint = OceanBluePrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Recordar", color = OceanBluePrimary, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}
