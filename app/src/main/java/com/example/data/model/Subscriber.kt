package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscribers")
data class Subscriber(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountNumber: String, // e.g. "CTA-1001"
    val name: String,
    val phone: String, // For SMS / WhatsApp reminders
    val address: String,
    val meterNumber: String, // Número de medidor
    val category: String = "Residencial", // Residencial, Comercial, Industrial
    val active: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
