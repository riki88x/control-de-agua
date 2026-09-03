package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ReminderLog
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderLogDao {
    @Query("SELECT * FROM reminder_logs ORDER BY sentTimestamp DESC")
    fun getAllLogs(): Flow<List<ReminderLog>>

    @Query("SELECT * FROM reminder_logs WHERE receiptId = :receiptId ORDER BY sentTimestamp DESC")
    fun getLogsForReceipt(receiptId: Long): Flow<List<ReminderLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ReminderLog): Long
}
