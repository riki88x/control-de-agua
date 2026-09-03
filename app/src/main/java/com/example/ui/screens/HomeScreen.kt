package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReceiptWithSubscriber
import com.example.ui.components.ReceiptCard
import com.example.ui.components.StatCard
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Person
import com.example.ui.theme.NaturalBorderOutline
import com.example.ui.theme.NaturalOutlineVariant
import com.example.ui.theme.NaturalSlate
import com.example.ui.theme.NaturalSoftBlueSection
import com.example.ui.theme.NaturalTextMuted
import com.example.ui.theme.NaturalTextPrimary
import com.example.ui.theme.NaturalTextSecondary
import com.example.ui.theme.OceanBlueContainer
import com.example.ui.theme.OceanBlueOnContainer
import com.example.ui.theme.OceanBluePrimary
import com.example.ui.theme.StatusOverdue
import com.example.ui.theme.StatusPaid
import com.example.ui.theme.StatusPending
import com.example.ui.viewmodel.BillingSummary
import java.util.Locale

@Composable
fun HomeScreen(
    summary: BillingSummary,
    overdueList: List<ReceiptWithSubscriber>,
    onNavigateToReceipts: () -> Unit,
    onNavigateToCobranza: () -> Unit,
    onNavigateToReminders: () -> Unit,
    onNewReceiptClick: () -> Unit,
    onPayReceipt: (ReceiptWithSubscriber) -> Unit,
    onSendReminder: (ReceiptWithSubscriber) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Natural Tones Header & Hero
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(OceanBlueContainer, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = OceanBlueOnContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "AguaViva",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = NaturalTextPrimary
                            )
                            Text(
                                text = "Cobranza y Facturación de Agua",
                                style = MaterialTheme.typography.bodySmall,
                                color = NaturalTextSecondary
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = NaturalOutlineVariant,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Perfil",
                                tint = NaturalTextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Hero Section: Cartera Pendiente (rounded-[28px], deep ocean blue)
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = OceanBluePrimary,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp)
                    ) {
                        Text(
                            text = "CARTERA PENDIENTE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.9f),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = String.format(Locale.US, "$%.2f", summary.totalPending),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            ),
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Collection progress indicator
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Efectividad de Cobro",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                                Text(
                                    text = "${summary.collectionRatePercent}%",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { (summary.collectionRatePercent / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp),
                                color = OceanBlueContainer,
                                trackColor = Color.White.copy(alpha = 0.25f),
                                strokeCap = StrokeCap.Round
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Bottom row with debtor count and pill "Gestionar" button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${summary.pendingCount} usuarios pendientes",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(Color(0xFF94A3B8), CircleShape)
                                            .border(1.5.dp, OceanBluePrimary, CircleShape)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(Color(0xFF64748B), CircleShape)
                                            .border(1.5.dp, OceanBluePrimary, CircleShape)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(Color(0xFF475569), CircleShape)
                                            .border(1.5.dp, OceanBluePrimary, CircleShape)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(OceanBlueContainer, CircleShape)
                                            .border(1.5.dp, OceanBluePrimary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "+${summary.pendingCount}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = OceanBlueOnContainer
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = onNavigateToCobranza,
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = OceanBlueContainer,
                                    contentColor = OceanBlueOnContainer
                                ),
                                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                                modifier = Modifier.testTag("hero_manage_button")
                            ) {
                                Text(
                                    text = "Gestionar",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Natural Soft Blue Section: Recordatorios Automáticos
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = NaturalSoftBlueSection,
                    border = BorderStroke(1.dp, OceanBlueContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToReminders() }
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = null,
                                    tint = OceanBluePrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Recordatorios Automáticos",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = NaturalTextPrimary
                                )
                            }

                            // Switch indicator
                            Box(
                                modifier = Modifier
                                    .size(width = 40.dp, height = 22.dp)
                                    .background(OceanBluePrimary, RoundedCornerShape(11.dp))
                                    .padding(horizontal = 3.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(Color.White, CircleShape)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (summary.overdueCount > 0)
                                "Envío listo para ${summary.overdueCount} usuarios con recibos vencidos por WhatsApp y SMS."
                            else
                                "Programado: Recordatorios preventivos antes de la fecha límite de pago.",
                            style = MaterialTheme.typography.bodySmall,
                            color = NaturalTextSecondary,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }
        }

        // 4 KPI Summary Cards Grid
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                Text(
                    text = "Resumen Financiero",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Recaudado",
                        value = String.format(Locale.US, "$%.2f", summary.totalCollected),
                        subtitle = "${summary.paidCount} recibos pagados",
                        icon = Icons.Default.AttachMoney,
                        iconColor = StatusPaid,
                        iconBgColor = StatusPaid.copy(alpha = 0.15f),
                        modifier = Modifier.weight(1f),
                        testTag = "kpi_recaudado"
                    )
                    StatCard(
                        title = "Por Cobrar",
                        value = String.format(Locale.US, "$%.2f", summary.totalPending),
                        subtitle = "${summary.pendingCount} pendientes",
                        icon = Icons.Default.Payment,
                        iconColor = StatusPending,
                        iconBgColor = StatusPending.copy(alpha = 0.15f),
                        modifier = Modifier.weight(1f),
                        testTag = "kpi_por_cobrar"
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Saldo Vencido",
                        value = String.format(Locale.US, "$%.2f", summary.totalOverdue),
                        subtitle = "${summary.overdueCount} cuentas en mora",
                        icon = Icons.Default.ErrorOutline,
                        iconColor = StatusOverdue,
                        iconBgColor = StatusOverdue.copy(alpha = 0.15f),
                        modifier = Modifier.weight(1f),
                        testTag = "kpi_vencido"
                    )
                    StatCard(
                        title = "Total Facturado",
                        value = String.format(Locale.US, "$%.2f", summary.totalBilled),
                        subtitle = "${summary.totalReceiptsCount} recibos emitidos",
                        icon = Icons.Default.WaterDrop,
                        iconColor = OceanBluePrimary,
                        iconBgColor = OceanBluePrimary.copy(alpha = 0.15f),
                        modifier = Modifier.weight(1f),
                        testTag = "kpi_facturado"
                    )
                }
            }
        }

        // Quick Actions Row
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "Acciones Rápidas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionButton(
                        title = "Nuevo Recibo",
                        icon = Icons.Default.Add,
                        backgroundColor = OceanBluePrimary,
                        onClick = onNewReceiptClick,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_new_receipt"
                    )
                    QuickActionButton(
                        title = "Cobranzas",
                        icon = Icons.Default.Payment,
                        backgroundColor = NaturalSlate,
                        onClick = onNavigateToCobranza,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_cobranzas"
                    )
                    QuickActionButton(
                        title = "Enviar Avisos",
                        icon = Icons.Default.NotificationsActive,
                        backgroundColor = OceanBluePrimary.copy(alpha = 0.85f),
                        onClick = onNavigateToReminders,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_recordatorios"
                    )
                }
            }
        }

        // Attention needed section: Overdue bills requiring reminders or collection
        item {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Atención Requerida (Cobros Vencidos)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (overdueList.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = StatusOverdue.copy(alpha = 0.15f),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "${overdueList.size}",
                                    color = StatusOverdue,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "Ver todos",
                        style = MaterialTheme.typography.labelMedium,
                        color = OceanBluePrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { onNavigateToCobranza() }
                            .padding(4.dp)
                    )
                }
            }
        }

        if (overdueList.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("✨", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "¡Excelente! No hay recibos vencidos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Todos los cobros de agua se encuentran al corriente.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        } else {
            items(overdueList.take(3), key = { "home_overdue_${it.receipt.id}" }) { item ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    ReceiptCard(
                        item = item,
                        onPayClick = { onPayReceipt(item) },
                        onReminderClick = { onSendReminder(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    title: String,
    icon: ImageVector,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "quick_action"
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        modifier = modifier
            .height(56.dp)
            .testTag(testTag),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )
        }
    }
}
