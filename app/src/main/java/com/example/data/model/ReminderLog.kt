package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder_logs")
data class ReminderLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val receiptId: Long,
    val subscriberId: Long,
    val channel: String, // "WHATSAPP", "SMS"
    val recipientPhone: String,
    val messageText: String,
    val sentTimestamp: Long = System.currentTimeMillis()
)
