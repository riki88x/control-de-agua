package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Receipt
import com.example.data.model.ReceiptWithSubscriber
import com.example.ui.theme.NaturalBorderOutline
import com.example.ui.theme.NaturalSurfaceVariant
import com.example.ui.theme.NaturalTextMuted
import com.example.ui.theme.NaturalTextPrimary
import com.example.ui.theme.NaturalTextSecondary
import com.example.ui.theme.OceanBlueContainer
import com.example.ui.theme.OceanBluePrimary
import com.example.ui.theme.StatusOverdue
import com.example.ui.theme.StatusPaid
import com.example.ui.theme.StatusPartial
import com.example.ui.theme.StatusPending
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReceiptCard(
    item: ReceiptWithSubscriber,
    onPayClick: () -> Unit,
    onReminderClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val r = item.receipt
    val sub = item.subscriber
    val isOverdue = r.isOverdue
    val isPaid = r.status == Receipt.STATUS_PAID
    val isPartial = r.status == Receipt.STATUS_PARTIAL

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dueDateStr = dateFormat.format(Date(r.dueDate))

    val statusColor = when {
        isPaid -> StatusPaid
        isOverdue -> StatusOverdue
        isPartial -> StatusPartial
        else -> StatusPending
    }

    val statusText = when {
        isPaid -> "PAGADO"
        isOverdue -> "VENCIDO"
        isPartial -> "PARCIAL"
        else -> "PENDIENTE"
    }

    val initials = sub.name
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")
        .ifEmpty { "AG" }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("receipt_card_${r.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, NaturalBorderOutline.copy(alpha = 0.8f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Avatar + Subscriber info + Amount / Status (like design HTML)
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
                        text = if (sub.address.isNotBlank()) sub.address else "Cuenta: ${sub.accountNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NaturalTextMuted,
                        letterSpacing = 0.4.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = String.format(Locale.US, "$%.2f", if (isPaid) r.totalAmount else r.pendingBalance),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isPaid) StatusPaid else if (isOverdue) StatusOverdue else NaturalTextPrimary
                    )
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub-details: Recibo, período y categoría
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${r.receiptNumber} • ${r.period}",
                    style = MaterialTheme.typography.bodySmall,
                    color = NaturalTextSecondary
                )
                Text(
                    text = "Medidor: ${sub.meterNumber} (${sub.category})",
                    style = MaterialTheme.typography.bodySmall,
                    color = NaturalTextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Readings & Consumption row
            Surface(
                color = NaturalSurfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Lecturas: ${r.previousReading} → ${r.currentReading} m³",
                            style = MaterialTheme.typography.bodySmall,
                            color = NaturalTextSecondary
                        )
                        Text(
                            text = "Vence: $dueDateStr",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Normal,
                            color = if (isOverdue) StatusOverdue else NaturalTextSecondary
                        )
                    }
                    Surface(
                        color = OceanBlueContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = String.format(Locale.US, "%.1f m³", r.consumptionM3),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = OceanBluePrimary
                        )
                    }
                }
            }

            if (r.paidAmount > 0 && !isPaid) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total: $${String.format(Locale.US, "%.2f", r.totalAmount)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = NaturalTextMuted
                    )
                    Text(
                        text = "Abonado: $${String.format(Locale.US, "%.2f", r.paidAmount)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = StatusPaid,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = NaturalBorderOutline.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Pay action
                    if (!isPaid) {
                        Button(
                            onClick = onPayClick,
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StatusPaid,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("pay_button_${r.id}")
                        ) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cobrar", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    // Reminder action
                    OutlinedButton(
                        onClick = onReminderClick,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, NaturalBorderOutline),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("reminder_button_${r.id}")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = OceanBluePrimary, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isPaid) "Comprobante" else "Recordar",
                            color = OceanBluePrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (onDeleteClick != null) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Eliminar recibo",
                            tint = StatusOverdue.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
