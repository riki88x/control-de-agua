package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.WaterDatabase
import com.example.data.model.Payment
import com.example.data.model.Receipt
import com.example.data.model.ReceiptWithSubscriber
import com.example.data.model.ReminderLog
import com.example.data.model.ReminderTemplateHelper
import com.example.data.model.ReminderType
import com.example.data.model.Subscriber
import com.example.data.repository.WaterBillingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BillingSummary(
    val totalBilled: Double = 0.0,
    val totalCollected: Double = 0.0,
    val totalPending: Double = 0.0,
    val totalOverdue: Double = 0.0,
    val totalReceiptsCount: Int = 0,
    val pendingCount: Int = 0,
    val overdueCount: Int = 0,
    val paidCount: Int = 0,
    val collectionRatePercent: Int = 0
)

class WaterBillingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WaterBillingRepository

    init {
        val db = WaterDatabase.getDatabase(application, viewModelScope)
        repository = WaterBillingRepository(
            db.subscriberDao(),
            db.receiptDao(),
            db.paymentDao(),
            db.reminderLogDao()
        )
    }

    val subscribers: StateFlow<List<Subscriber>> = repository.allSubscribers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val receipts: StateFlow<List<ReceiptWithSubscriber>> = repository.allReceipts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payments: StateFlow<List<Payment>> = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reminderLogs: StateFlow<List<ReminderLog>> = repository.allReminderLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and filter state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow("TODOS") // TODOS, PENDIENTES, VENCIDOS, PAGADOS
    val selectedStatusFilter: StateFlow<String> = _selectedStatusFilter.asStateFlow()

    // Filtered receipts
    val filteredReceipts: StateFlow<List<ReceiptWithSubscriber>> = combine(
        receipts,
        searchQuery,
        selectedStatusFilter
    ) { list, query, filter ->
        list.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.subscriber.name.contains(query, ignoreCase = true) ||
                    item.subscriber.accountNumber.contains(query, ignoreCase = true) ||
                    item.receipt.receiptNumber.contains(query, ignoreCase = true) ||
                    item.subscriber.meterNumber.contains(query, ignoreCase = true)

            val now = System.currentTimeMillis()
            val matchesStatus = when (filter) {
                "PENDIENTES" -> item.receipt.status != Receipt.STATUS_PAID
                "VENCIDOS" -> item.receipt.status != Receipt.STATUS_PAID && item.receipt.dueDate < now
                "PAGADOS" -> item.receipt.status == Receipt.STATUS_PAID
                else -> true
            }

            matchesQuery && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Overdue or pending items specifically for collection view
    val pendingDebtors: StateFlow<List<ReceiptWithSubscriber>> = receipts.combine(_searchQuery) { list, query ->
        val now = System.currentTimeMillis()
        list.filter { item ->
            val isUnpaid = item.receipt.status != Receipt.STATUS_PAID && item.receipt.pendingBalance > 0
            val matchesQuery = query.isBlank() ||
                    item.subscriber.name.contains(query, ignoreCase = true) ||
                    item.subscriber.accountNumber.contains(query, ignoreCase = true) ||
                    item.subscriber.meterNumber.contains(query, ignoreCase = true)
            isUnpaid && matchesQuery
        }.sortedByDescending { it.receipt.dueDate < now }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calculation summary KPI
    val summary: StateFlow<BillingSummary> = receipts.combine(payments) { receiptList, _ ->
        val now = System.currentTimeMillis()
        var totalBilled = 0.0
        var totalCollected = 0.0
        var totalPending = 0.0
        var totalOverdue = 0.0
        var pendingCount = 0
        var overdueCount = 0
        var paidCount = 0

        for (item in receiptList) {
            val r = item.receipt
            totalBilled += r.totalAmount
            totalCollected += r.paidAmount
            val pending = r.pendingBalance

            if (r.status == Receipt.STATUS_PAID) {
                paidCount++
            } else {
                pendingCount++
                totalPending += pending
                if (r.dueDate < now) {
                    overdueCount++
                    totalOverdue += pending
                }
            }
        }

        val rate = if (totalBilled > 0) ((totalCollected / totalBilled) * 100).toInt() else 0

        BillingSummary(
            totalBilled = totalBilled,
            totalCollected = totalCollected,
            totalPending = totalPending,
            totalOverdue = totalOverdue,
            totalReceiptsCount = receiptList.size,
            pendingCount = pendingCount,
            overdueCount = overdueCount,
            paidCount = paidCount,
            collectionRatePercent = rate
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BillingSummary())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(filter: String) {
        _selectedStatusFilter.value = filter
    }

    // --- Actions ---

    fun addSubscriber(
        accountNumber: String,
        name: String,
        phone: String,
        address: String,
        meterNumber: String,
        category: String
    ) {
        viewModelScope.launch {
            repository.insertSubscriber(
                Subscriber(
                    accountNumber = accountNumber.ifBlank { "CTA-${System.currentTimeMillis() % 10000}" },
                    name = name,
                    phone = phone,
                    address = address,
                    meterNumber = meterNumber.ifBlank { "MED-${System.currentTimeMillis() % 10000}" },
                    category = category
                )
            )
        }
    }

    fun updateSubscriber(subscriber: Subscriber) {
        viewModelScope.launch {
            repository.updateSubscriber(subscriber)
        }
    }

    fun deleteSubscriber(subscriber: Subscriber) {
        viewModelScope.launch {
            repository.deleteSubscriber(subscriber)
        }
    }

    fun addReceipt(
        subscriberId: Long,
        period: String,
        previousReading: Double,
        currentReading: Double,
        pricePerM3: Double,
        fixedFee: Double,
        sewerFee: Double,
        dueDate: Long,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val consumption = (currentReading - previousReading).coerceAtLeast(0.0)
            val waterCost = consumption * pricePerM3
            val total = waterCost + fixedFee + sewerFee
            val receiptNumber = "REC-${System.currentTimeMillis() % 100000}"

            val now = System.currentTimeMillis()
            val initialStatus = if (now > dueDate) Receipt.STATUS_OVERDUE else Receipt.STATUS_PENDING

            val receipt = Receipt(
                receiptNumber = receiptNumber,
                subscriberId = subscriberId,
                period = period,
                issueDate = now,
                dueDate = dueDate,
                previousReading = previousReading,
                currentReading = currentReading,
                consumptionM3 = consumption,
                pricePerM3 = pricePerM3,
                fixedFee = fixedFee,
                sewerFee = sewerFee,
                totalAmount = total,
                paidAmount = 0.0,
                status = initialStatus,
                notes = notes
            )
            repository.insertReceipt(receipt)
        }
    }

    fun recordPayment(
        receiptId: Long,
        subscriberId: Long,
        amount: Double,
        method: String,
        reference: String,
        receivedBy: String,
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            repository.registerPayment(
                receiptId = receiptId,
                subscriberId = subscriberId,
                amount = amount,
                method = method,
                reference = reference,
                receivedBy = receivedBy
            )
            onSuccess?.invoke()
        }
    }

    fun deleteReceipt(receipt: Receipt) {
        viewModelScope.launch {
            repository.deleteReceipt(receipt)
        }
    }

    // --- Message Sending Dispatchers ---

    fun sendReminderMessage(
        context: Context,
        subscriber: Subscriber,
        receipt: Receipt,
        type: ReminderType,
        channel: String, // "WHATSAPP" or "SMS"
        customMessage: String? = null
    ) {
        val message = customMessage ?: ReminderTemplateHelper.generateMessage(type, subscriber, receipt)
        val cleanPhone = subscriber.phone.replace(Regex("[^0-9+]"), "")

        try {
            if (channel == "WHATSAPP") {
                val whatsappUrl = "https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(message)}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl)).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } else {
                // SMS
                val smsUri = Uri.parse("smsto:$cleanPhone")
                val intent = Intent(Intent.ACTION_SENDTO, smsUri).apply {
                    putExtra("sms_body", message)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }

            // Log reminder in Room database
            viewModelScope.launch {
                repository.logReminder(
                    receiptId = receipt.id,
                    subscriberId = subscriber.id,
                    channel = channel,
                    phone = subscriber.phone,
                    messageText = message
                )
            }
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo abrir la aplicación de mensajes: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
