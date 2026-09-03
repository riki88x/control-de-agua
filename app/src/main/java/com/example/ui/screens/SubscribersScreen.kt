package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReceiptWithSubscriber
import com.example.data.model.Subscriber
import com.example.ui.theme.NaturalBorderOutline
import com.example.ui.theme.NaturalSurfaceVariant
import com.example.ui.theme.NaturalTextMuted
import com.example.ui.theme.NaturalTextPrimary
import com.example.ui.theme.NaturalTextSecondary
import com.example.ui.theme.OceanBlueContainer
import com.example.ui.theme.OceanBluePrimary
import com.example.ui.theme.StatusOverdue
import com.example.ui.theme.StatusPaid
import java.util.Locale

@Composable
fun SubscribersScreen(
    subscribers: List<Subscriber>,
    receipts: List<ReceiptWithSubscriber>,
    onAddSubscriberClick: () -> Unit,
    onDeleteSubscriber: (Subscriber) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var subscriberToDelete by remember { mutableStateOf<Subscriber?>(null) }

    val filteredList = subscribers.filter { sub ->
        searchQuery.isBlank() ||
                sub.name.contains(searchQuery, ignoreCase = true) ||
                sub.accountNumber.contains(searchQuery, ignoreCase = true) ||
                sub.meterNumber.contains(searchQuery, ignoreCase = true) ||
                sub.address.contains(searchQuery, ignoreCase = true)
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("subscribers_screen"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            item {
                Column {
                    Text(
                        text = "Padrón de Suscriptores",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = NaturalTextPrimary
                    )
                    Text(
                        text = "Catálogo de usuarios del servicio, medidores y contratos",
                        style = MaterialTheme.typography.bodySmall,
                        color = NaturalTextSecondary
                    )
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("subscriber_search_bar"),
                    placeholder = { Text("Buscar por nombre, contrato, medidor...", color = NaturalTextMuted) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = OceanBluePrimary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpiar", tint = NaturalTextSecondary)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = OceanBluePrimary,
                        unfocusedBorderColor = NaturalBorderOutline
                    )
                )
            }

            // Total count
            item {
                Text(
                    text = "${filteredList.size} suscriptores registrados",
                    style = MaterialTheme.typography.bodySmall,
                    color = NaturalTextSecondary
                )
            }

            // Subscriber Cards
            items(filteredList, key = { it.id }) { sub ->
                // Calculate total pending debt for this subscriber
                val subDebt = receipts
                    .filter { it.subscriber.id == sub.id && it.receipt.status != "PAGADO" }
                    .sumOf { it.receipt.pendingBalance }

                val initials = sub.name
                    .split(" ")
                    .filter { it.isNotBlank() }
                    .take(2)
                    .mapNotNull { it.firstOrNull()?.uppercase() }
                    .joinToString("")
                    .ifEmpty { "SU" }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("subscriber_card_${sub.id}"),
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
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
                                Column {
                                    Text(
                                        text = sub.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = NaturalTextPrimary
                                    )
                                    Text(
                                        text = "Contrato: ${sub.accountNumber}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NaturalTextSecondary
                                    )
                                }
                            }

                            // Category badge
                            Surface(
                                color = NaturalSurfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = sub.category,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = NaturalTextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Details: Address & Meter
                        Text(
                            text = "📍 ${sub.address}",
                            style = MaterialTheme.typography.bodySmall,
                            color = NaturalTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = OceanBluePrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Medidor: ${sub.meterNumber}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = NaturalTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Debt & Action Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (subDebt > 0) "Saldo Deudor:" else "Estado:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (subDebt > 0) StatusOverdue else StatusPaid
                                )
                                Text(
                                    text = if (subDebt > 0) String.format(Locale.US, "$%.2f MXN", subDebt) else "Al Corriente",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (subDebt > 0) StatusOverdue else StatusPaid
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                // Direct Call button
                                IconButton(
                                    onClick = {
                                        val cleanPhone = sub.phone.replace(Regex("[^0-9+]"), "")
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanPhone"))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(OceanBlueContainer, CircleShape)
                                ) {
                                    Icon(
                                        Icons.Default.Call,
                                        contentDescription = "Llamar",
                                        tint = OceanBluePrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Delete subscriber
                                IconButton(
                                    onClick = { subscriberToDelete = sub },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        Icons.Default.DeleteOutline,
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button to add subscriber
        FloatingActionButton(
            onClick = onAddSubscriberClick,
            shape = RoundedCornerShape(18.dp),
            containerColor = OceanBluePrimary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 88.dp, end = 20.dp)
                .testTag("fab_add_subscriber")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Nuevo Suscriptor", fontWeight = FontWeight.Bold)
            }
        }

        // Delete confirmation dialog
        if (subscriberToDelete != null) {
            AlertDialog(
                onDismissRequest = { subscriberToDelete = null },
                title = { Text("¿Eliminar Suscriptor?") },
                text = {
                    Text("Se eliminará a ${subscriberToDelete!!.name} y sus recibos asociados.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeleteSubscriber(subscriberToDelete!!)
                            subscriberToDelete = null
                        }
                    ) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { subscriberToDelete = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
