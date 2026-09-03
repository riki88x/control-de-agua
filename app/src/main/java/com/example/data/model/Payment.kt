package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = Receipt::class,
            parentColumns = ["id"],
            childColumns = ["receiptId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("receiptId"), Index("subscriberId")]
)
data class Payment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val receiptId: Long,
    val subscriberId: Long,
    val amount: Double,
    val paymentDate: Long = System.currentTimeMillis(),
    val paymentMethod: String = "Efectivo", // Efectivo, Transferencia, Tarjeta, Depósito
    val reference: String = "",
    val receivedBy: String = "Administración"
)
