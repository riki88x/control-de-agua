package com.example.data.repository

import com.example.data.db.PaymentDao
import com.example.data.db.ReceiptDao
import com.example.data.db.ReminderLogDao
import com.example.data.db.SubscriberDao
import com.example.data.model.Payment
import com.example.data.model.Receipt
import com.example.data.model.ReceiptWithSubscriber
import com.example.data.model.ReminderLog
import com.example.data.model.Subscriber
import kotlinx.coroutines.flow.Flow

class WaterBillingRepository(
    private val subscriberDao: SubscriberDao,
    private val receiptDao: ReceiptDao,
    private val paymentDao: PaymentDao,
    private val reminderLogDao: ReminderLogDao
) {
    val allSubscribers: Flow<List<Subscriber>> = subscriberDao.getAllSubscribers()
    val subscriberCount: Flow<Int> = subscriberDao.getSubscriberCount()
    val allReceipts: Flow<List<ReceiptWithSubscriber>> = receiptDao.getAllReceiptsWithSubscriber()
    val pendingReceipts: Flow<List<ReceiptWithSubscriber>> = receiptDao.getPendingReceipts()
    val allPayments: Flow<List<Payment>> = paymentDao.getAllPayments()
    val allReminderLogs: Flow<List<ReminderLog>> = reminderLogDao.getAllLogs()

    fun getReceiptsForSubscriber(subscriberId: Long): Flow<List<ReceiptWithSubscriber>> {
        return receiptDao.getReceiptsForSubscriber(subscriberId)
    }

    fun getPaymentsForReceipt(receiptId: Long): Flow<List<Payment>> {
        return paymentDao.getPaymentsForReceipt(receiptId)
    }

    suspend fun insertSubscriber(subscriber: Subscriber): Long {
        return subscriberDao.insertSubscriber(subscriber)
    }

    suspend fun updateSubscriber(subscriber: Subscriber) {
        subscriberDao.updateSubscriber(subscriber)
    }

    suspend fun deleteSubscriber(subscriber: Subscriber) {
        subscriberDao.deleteSubscriber(subscriber)
    }

    suspend fun insertReceipt(receipt: Receipt): Long {
        return receiptDao.insertReceipt(receipt)
    }

    suspend fun updateReceipt(receipt: Receipt) {
        receiptDao.updateReceipt(receipt)
    }

    suspend fun deleteReceipt(receipt: Receipt) {
        receiptDao.deleteReceipt(receipt)
    }

    suspend fun registerPayment(
        receiptId: Long,
        subscriberId: Long,
        amount: Double,
        method: String,
        reference: String,
        receivedBy: String
    ): Payment? {
        val receipt = receiptDao.getReceiptByIdOnce(receiptId) ?: return null
        val newPaidAmount = receipt.paidAmount + amount
        val newStatus = if (newPaidAmount >= receipt.totalAmount - 0.01) {
            Receipt.STATUS_PAID
        } else {
            Receipt.STATUS_PARTIAL
        }

        receiptDao.updatePayment(receiptId, newPaidAmount, newStatus)

        val payment = Payment(
            receiptId = receiptId,
            subscriberId = subscriberId,
            amount = amount,
            paymentDate = System.currentTimeMillis(),
            paymentMethod = method,
            reference = reference,
            receivedBy = receivedBy
        )
        val id = paymentDao.insertPayment(payment)
        return payment.copy(id = id)
    }

    suspend fun logReminder(
        receiptId: Long,
        subscriberId: Long,
        channel: String,
        phone: String,
        messageText: String
    ) {
        val now = System.currentTimeMillis()
        receiptDao.updateReminderSent(receiptId, now)
        reminderLogDao.insertLog(
            ReminderLog(
                receiptId = receiptId,
                subscriberId = subscriberId,
                channel = channel,
                recipientPhone = phone,
                messageText = messageText,
                sentTimestamp = now
            )
        )
    }
}
