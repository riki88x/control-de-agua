package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.Receipt
import com.example.data.model.ReceiptWithSubscriber
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {
    @Transaction
    @Query("SELECT * FROM receipts ORDER BY dueDate ASC, id DESC")
    fun getAllReceiptsWithSubscriber(): Flow<List<ReceiptWithSubscriber>>

    @Transaction
    @Query("SELECT * FROM receipts WHERE status != 'PAGADO' ORDER BY dueDate ASC")
    fun getPendingReceipts(): Flow<List<ReceiptWithSubscriber>>

    @Transaction
    @Query("SELECT * FROM receipts WHERE subscriberId = :subscriberId ORDER BY issueDate DESC")
    fun getReceiptsForSubscriber(subscriberId: Long): Flow<List<ReceiptWithSubscriber>>

    @Transaction
    @Query("SELECT * FROM receipts WHERE id = :id")
    fun getReceiptWithSubscriberById(id: Long): Flow<ReceiptWithSubscriber?>

    @Query("SELECT * FROM receipts WHERE id = :id")
    suspend fun getReceiptByIdOnce(id: Long): Receipt?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt: Receipt): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipts(receipts: List<Receipt>): List<Long>

    @Update
    suspend fun updateReceipt(receipt: Receipt)

    @Query("UPDATE receipts SET lastReminderSentDate = :timestamp WHERE id = :receiptId")
    suspend fun updateReminderSent(receiptId: Long, timestamp: Long)

    @Query("UPDATE receipts SET paidAmount = :paidAmount, status = :status WHERE id = :receiptId")
    suspend fun updatePayment(receiptId: Long, paidAmount: Double, status: String)

    @Delete
    suspend fun deleteReceipt(receipt: Receipt)

    @Query("SELECT COUNT(*) FROM receipts")
    suspend fun getCount(): Int
}
