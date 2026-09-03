package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import com.example.ui.theme.NaturalBorderOutline
import com.example.ui.theme.NaturalTextSecondary
import com.example.ui.theme.OceanBlueContainer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ReceiptWithSubscriber
import com.example.data.model.ReminderType
import com.example.ui.components.AddReceiptDialog
import com.example.ui.components.AddSubscriberDialog
import com.example.ui.components.PaymentDialog
import com.example.ui.components.SendReminderDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PendingPaymentsScreen
import com.example.ui.screens.ReceiptsScreen
import com.example.ui.screens.RemindersScreen
import com.example.ui.screens.SubscribersScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.OceanBluePrimary
import com.example.ui.theme.StatusOverdue
import com.example.ui.viewmodel.WaterBillingViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: WaterBillingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                WaterBillingApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun WaterBillingApp(viewModel: WaterBillingViewModel) {
    val context = LocalContext.current

    var selectedTab by remember { mutableIntStateOf(0) }

    // Dialog state
    var showAddReceiptDialog by remember { mutableStateOf(false) }
    var showAddSubscriberDialog by remember { mutableStateOf(false) }
    var activePaymentReceipt by remember { mutableStateOf<ReceiptWithSubscriber?>(null) }
    var activeReminderReceipt by remember { mutableStateOf<ReceiptWithSubscriber?>(null) }

    // State from ViewModel
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val receipts by viewModel.receipts.collectAsStateWithLifecycle()
    val filteredReceipts by viewModel.filteredReceipts.collectAsStateWithLifecycle()
    val pendingDebtors by viewModel.pendingDebtors.collectAsStateWithLifecycle()
    val subscribers by viewModel.subscribers.collectAsStateWithLifecycle()
    val reminderLogs by viewModel.reminderLogs.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedStatusFilter by viewModel.selectedStatusFilter.collectAsStateWithLifecycle()

    val overdueCount = summary.overdueCount

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Column {
                HorizontalDivider(color = NaturalBorderOutline, thickness = 1.dp)
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 0.dp,
                    modifier = Modifier.testTag("main_navigation_bar")
                ) {
                    val navItemColors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OceanBluePrimary,
                        selectedTextColor = OceanBluePrimary,
                        indicatorColor = OceanBlueContainer,
                        unselectedIconColor = NaturalTextSecondary,
                        unselectedTextColor = NaturalTextSecondary
                    )

                    // Tab 0: Inicio
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                        label = { Text("Inicio", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                        colors = navItemColors,
                        modifier = Modifier.testTag("nav_inicio")
                    )

                    // Tab 1: Recibos
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Recibos") },
                        label = { Text("Recibos", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                        colors = navItemColors,
                        modifier = Modifier.testTag("nav_recibos")
                    )

                    // Tab 2: Cobranzas
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {
                            if (overdueCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(containerColor = StatusOverdue) {
                                            Text("$overdueCount")
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.CreditCard, contentDescription = "Cobranzas")
                                }
                            } else {
                                Icon(Icons.Default.CreditCard, contentDescription = "Cobranzas")
                            }
                        },
                        label = { Text("Cobranza", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                        colors = navItemColors,
                        modifier = Modifier.testTag("nav_cobranza")
                    )

                    // Tab 3: Recordatorios
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = {
                            Icon(Icons.Default.NotificationsActive, contentDescription = "Recordatorios")
                        },
                        label = { Text("Avisos", fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
                        colors = navItemColors,
                        modifier = Modifier.testTag("nav_recordatorios")
                    )

                    // Tab 4: Suscriptores
                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = { selectedTab = 4 },
                        icon = { Icon(Icons.Default.People, contentDescription = "Suscriptores") },
                        label = { Text("Clientes", fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal) },
                        colors = navItemColors,
                        modifier = Modifier.testTag("nav_suscriptores")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
                    summary = summary,
                    overdueList = pendingDebtors.filter { it.receipt.isOverdue },
                    onNavigateToReceipts = { selectedTab = 1 },
                    onNavigateToCobranza = { selectedTab = 2 },
                    onNavigateToReminders = { selectedTab = 3 },
                    onNewReceiptClick = { showAddReceiptDialog = true },
                    onPayReceipt = { activePaymentReceipt = it },
                    onSendReminder = { activeReminderReceipt = it }
                )
                1 -> ReceiptsScreen(
                    receipts = filteredReceipts,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    selectedFilter = selectedStatusFilter,
                    onFilterChange = { viewModel.setStatusFilter(it) },
                    onNewReceiptClick = { showAddReceiptDialog = true },
                    onPayReceipt = { activePaymentReceipt = it },
                    onSendReminder = { activeReminderReceipt = it },
                    onDeleteReceipt = { viewModel.deleteReceipt(it.receipt) }
                )
                2 -> PendingPaymentsScreen(
                    debtorList = pendingDebtors,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    onPayReceipt = { activePaymentReceipt = it },
                    onSendReminder = { activeReminderReceipt = it }
                )
                3 -> RemindersScreen(
                    pendingDebtors = pendingDebtors,
                    reminderLogs = reminderLogs,
                    onSendReminder = { activeReminderReceipt = it },
                    onQuickSend = { item, channel, type ->
                        viewModel.sendReminderMessage(
                            context = context,
                            subscriber = item.subscriber,
                            receipt = item.receipt,
                            type = type,
                            channel = channel
                        )
                    }
                )
                4 -> SubscribersScreen(
                    subscribers = subscribers,
                    receipts = receipts,
                    onAddSubscriberClick = { showAddSubscriberDialog = true },
                    onDeleteSubscriber = { viewModel.deleteSubscriber(it) }
                )
            }
        }
    }

    // Modal Dialogs
    if (showAddReceiptDialog) {
        AddReceiptDialog(
            subscribers = subscribers,
            onDismiss = { showAddReceiptDialog = false },
            onSaveReceipt = { subscriberId, period, prev, curr, price, fixed, sewer, dueDate, notes ->
                viewModel.addReceipt(
                    subscriberId = subscriberId,
                    period = period,
                    previousReading = prev,
                    currentReading = curr,
                    pricePerM3 = price,
                    fixedFee = fixed,
                    sewerFee = sewer,
                    dueDate = dueDate,
                    notes = notes
                )
                showAddReceiptDialog = false
                Toast.makeText(context, "Recibo emitido con éxito", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showAddSubscriberDialog) {
        AddSubscriberDialog(
            onDismiss = { showAddSubscriberDialog = false },
            onSaveSubscriber = { account, name, phone, address, meter, category ->
                viewModel.addSubscriber(account, name, phone, address, meter, category)
                showAddSubscriberDialog = false
                Toast.makeText(context, "Suscriptor registrado con éxito", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (activePaymentReceipt != null) {
        PaymentDialog(
            item = activePaymentReceipt!!,
            onDismiss = { activePaymentReceipt = null },
            onConfirmPayment = { amount, method, ref, receiver ->
                val receiptItem = activePaymentReceipt!!
                viewModel.recordPayment(
                    receiptId = receiptItem.receipt.id,
                    subscriberId = receiptItem.subscriber.id,
                    amount = amount,
                    method = method,
                    reference = ref,
                    receivedBy = receiver,
                    onSuccess = {
                        Toast.makeText(context, "Pago de $${String.format(java.util.Locale.US, "%.2f", amount)} registrado", Toast.LENGTH_SHORT).show()
                    }
                )
                activePaymentReceipt = null
            }
        )
    }

    if (activeReminderReceipt != null) {
        SendReminderDialog(
            item = activeReminderReceipt!!,
            onDismiss = { activeReminderReceipt = null },
            onSendMessage = { channel, customMessage ->
                val item = activeReminderReceipt!!
                viewModel.sendReminderMessage(
                    context = context,
                    subscriber = item.subscriber,
                    receipt = item.receipt,
                    type = if (item.receipt.isOverdue) ReminderType.OVERDUE else ReminderType.UPCOMING,
                    channel = channel,
                    customMessage = customMessage
                )
                Toast.makeText(context, "Abriendo $channel...", Toast.LENGTH_SHORT).show()
                activeReminderReceipt = null
            }
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("Android") }
}
