package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "receipts",
    foreignKeys = [
        ForeignKey(
            entity = Subscriber::class,
            parentColumns = ["id"],
            childColumns = ["subscriberId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("subscriberId")]
)
data class Receipt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val receiptNumber: String,
    val subscriberId: Long,
    val period: String, // e.g. "Septiembre 2024"
    val issueDate: Long,
    val dueDate: Long,
    val previousReading: Double,
    val currentReading: Double,
    val consumptionM3: Double,
    val pricePerM3: Double,
    val fixedFee: Double,
    val sewerFee: Double,
    val totalAmount: Double,
    val paidAmount: Double = 0.0,
    val status: String = STATUS_PENDING,
    val lastReminderSentDate: Long? = null,
    val notes: String = ""
) {
    val pendingBalance: Double
        get() = (totalAmount - paidAmount).coerceAtLeast(0.0)

    val isOverdue: Boolean
        get() = status != STATUS_PAID && System.currentTimeMillis() > dueDate

    companion object {
        const val STATUS_PENDING = "PENDIENTE"
        const val STATUS_PAID = "PAGADO"
        const val STATUS_OVERDUE = "VENCIDO"
        const val STATUS_PARTIAL = "PARCIAL"
    }
}
