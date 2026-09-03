package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Payment
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY paymentDate DESC")
    fun getAllPayments(): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE receiptId = :receiptId ORDER BY paymentDate DESC")
    fun getPaymentsForReceipt(receiptId: Long): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE subscriberId = :subscriberId ORDER BY paymentDate DESC")
    fun getPaymentsForSubscriber(subscriberId: Long): Flow<List<Payment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: Payment): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments: List<Payment>): List<Long>
}
