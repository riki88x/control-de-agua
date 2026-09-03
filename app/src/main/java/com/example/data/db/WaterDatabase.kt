package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Payment
import com.example.data.model.Receipt
import com.example.data.model.ReminderLog
import com.example.data.model.Subscriber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(
    entities = [
        Subscriber::class,
        Receipt::class,
        Payment::class,
        ReminderLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WaterDatabase : RoomDatabase() {
    abstract fun subscriberDao(): SubscriberDao
    abstract fun receiptDao(): ReceiptDao
    abstract fun paymentDao(): PaymentDao
    abstract fun reminderLogDao(): ReminderLogDao

    companion object {
        @Volatile
        private var INSTANCE: WaterDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): WaterDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WaterDatabase::class.java,
                    "water_billing_database"
                )
                    .addCallback(WaterDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class WaterDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database)
                }
            }
        }

        suspend fun populateDatabase(database: WaterDatabase) {
            val subscriberDao = database.subscriberDao()
            val receiptDao = database.receiptDao()
            val paymentDao = database.paymentDao()

            val cal = Calendar.getInstance()
            val now = cal.timeInMillis

            // Sample subscribers with Mexican/Latin American realistic water utility data
            val subscribers = listOf(
                Subscriber(
                    id = 1,
                    accountNumber = "AG-00101",
                    name = "Carlos Mendoza García",
                    phone = "+52 55 1234 5678",
                    address = "Av. Las Fuentes #142, Col. Prados",
                    meterNumber = "MED-7821",
                    category = "Residencial"
                ),
                Subscriber(
                    id = 2,
                    accountNumber = "AG-00102",
                    name = "María Elena Flores",
                    phone = "+52 55 9876 5432",
                    address = "Calle Jacarandas #58, Col. Centro",
                    meterNumber = "MED-4519",
                    category = "Residencial"
                ),
                Subscriber(
                    id = 3,
                    accountNumber = "AG-00103",
                    name = "Roberto Soto Vega (Lavandería Aqua)",
                    phone = "+52 55 4433 2211",
                    address = "Blvd. Acueducto #890, Zona Comercial",
                    meterNumber = "MED-9902",
                    category = "Comercial"
                ),
                Subscriber(
                    id = 4,
                    accountNumber = "AG-00104",
                    name = "Gloria Ramírez Benítez",
                    phone = "+52 55 7788 9900",
                    address = "Calle Los Manantiales #312, Fracc. Las Palmas",
                    meterNumber = "MED-6340",
                    category = "Residencial"
                ),
                Subscriber(
                    id = 5,
                    accountNumber = "AG-00105",
                    name = "Taller Mecánico El Torito",
                    phone = "+52 55 6655 4433",
                    address = "Calzada del Río #45, Bodega 3",
                    meterNumber = "MED-8812",
                    category = "Comercial"
                ),
                Subscriber(
                    id = 6,
                    accountNumber = "AG-00106",
                    name = "Dr. Fernando Ruiz Ramos",
                    phone = "+52 55 3322 1100",
                    address = "Paseo del Valle #12, Col. Bellavista",
                    meterNumber = "MED-2104",
                    category = "Residencial"
                )
            )
            subscriberDao.insertSubscribers(subscribers)

            // Overdue date: 15 days ago
            cal.timeInMillis = now
            cal.add(Calendar.DAY_OF_YEAR, -15)
            val overdueDate = cal.timeInMillis

            // Upcoming date: in 5 days
            cal.timeInMillis = now
            cal.add(Calendar.DAY_OF_YEAR, 5)
            val upcomingDueDate = cal.timeInMillis

            // Future date: in 18 days
            cal.timeInMillis = now
            cal.add(Calendar.DAY_OF_YEAR, 18)
            val futureDueDate = cal.timeInMillis

            val receipts = listOf(
                // 1. Carlos Mendoza - Overdue bill (needs urgent reminder)
                Receipt(
                    id = 1,
                    receiptNumber = "REC-2024-0801",
                    subscriberId = 1,
                    period = "Agosto 2024",
                    issueDate = overdueDate - (30L * 86400000L),
                    dueDate = overdueDate,
                    previousReading = 142.0,
                    currentReading = 168.0,
                    consumptionM3 = 26.0,
                    pricePerM3 = 12.50,
                    fixedFee = 65.0,
                    sewerFee = 45.0,
                    totalAmount = 435.0,
                    paidAmount = 0.0,
                    status = Receipt.STATUS_OVERDUE,
                    lastReminderSentDate = null,
                    notes = "Recibo vencido con aviso pendiente"
                ),
                // 2. María Elena Flores - Overdue partial bill
                Receipt(
                    id = 2,
                    receiptNumber = "REC-2024-0802",
                    subscriberId = 2,
                    period = "Agosto 2024",
                    issueDate = overdueDate - (30L * 86400000L),
                    dueDate = overdueDate,
                    previousReading = 89.0,
                    currentReading = 104.0,
                    consumptionM3 = 15.0,
                    pricePerM3 = 12.50,
                    fixedFee = 65.0,
                    sewerFee = 35.0,
                    totalAmount = 287.50,
                    paidAmount = 100.0,
                    status = Receipt.STATUS_PARTIAL,
                    lastReminderSentDate = now - (2L * 86400000L),
                    notes = "Abonó $100.00 en caja"
                ),
                // 3. Roberto Soto (Lavandería) - Current pending bill
                Receipt(
                    id = 3,
                    receiptNumber = "REC-2024-0901",
                    subscriberId = 3,
                    period = "Septiembre 2024",
                    issueDate = now - (10L * 86400000L),
                    dueDate = upcomingDueDate,
                    previousReading = 520.0,
                    currentReading = 615.0,
                    consumptionM3 = 95.0,
                    pricePerM3 = 18.00,
                    fixedFee = 150.0,
                    sewerFee = 120.0,
                    totalAmount = 1980.0,
                    paidAmount = 0.0,
                    status = Receipt.STATUS_PENDING,
                    lastReminderSentDate = null,
                    notes = "Tarifa comercial de alto consumo"
                ),
                // 4. Gloria Ramírez - Current pending bill
                Receipt(
                    id = 4,
                    receiptNumber = "REC-2024-0902",
                    subscriberId = 4,
                    period = "Septiembre 2024",
                    issueDate = now - (10L * 86400000L),
                    dueDate = upcomingDueDate,
                    previousReading = 210.0,
                    currentReading = 228.0,
                    consumptionM3 = 18.0,
                    pricePerM3 = 12.50,
                    fixedFee = 65.0,
                    sewerFee = 40.0,
                    totalAmount = 330.0,
                    paidAmount = 0.0,
                    status = Receipt.STATUS_PENDING,
                    lastReminderSentDate = null,
                    notes = "Consumo regular familiar"
                ),
                // 5. Taller El Torito - Paid bill
                Receipt(
                    id = 5,
                    receiptNumber = "REC-2024-0903",
                    subscriberId = 5,
                    period = "Septiembre 2024",
                    issueDate = now - (12L * 86400000L),
                    dueDate = futureDueDate,
                    previousReading = 310.0,
                    currentReading = 345.0,
                    consumptionM3 = 35.0,
                    pricePerM3 = 18.00,
                    fixedFee = 150.0,
                    sewerFee = 80.0,
                    totalAmount = 860.0,
                    paidAmount = 860.0,
                    status = Receipt.STATUS_PAID,
                    lastReminderSentDate = null,
                    notes = "Liquidado puntualmente con transferencia"
                ),
                // 6. Dr. Fernando Ruiz - Paid bill
                Receipt(
                    id = 6,
                    receiptNumber = "REC-2024-0904",
                    subscriberId = 6,
                    period = "Septiembre 2024",
                    issueDate = now - (12L * 86400000L),
                    dueDate = futureDueDate,
                    previousReading = 95.0,
                    currentReading = 112.0,
                    consumptionM3 = 17.0,
                    pricePerM3 = 12.50,
                    fixedFee = 65.0,
                    sewerFee = 38.0,
                    totalAmount = 315.50,
                    paidAmount = 315.50,
                    status = Receipt.STATUS_PAID,
                    lastReminderSentDate = null,
                    notes = "Pagado en ventanilla bancaria"
                )
            )
            receiptDao.insertReceipts(receipts)

            // Initial payments
            val payments = listOf(
                Payment(
                    id = 1,
                    receiptId = 2,
                    subscriberId = 2,
                    amount = 100.0,
                    paymentDate = now - (2L * 86400000L),
                    paymentMethod = "Efectivo",
                    reference = "REC-ABONO-001",
                    receivedBy = "Caja Principal"
                ),
                Payment(
                    id = 2,
                    receiptId = 5,
                    subscriberId = 5,
                    amount = 860.0,
                    paymentDate = now - (5L * 86400000L),
                    paymentMethod = "Transferencia",
                    reference = "SPEI-9482710",
                    receivedBy = "Portal Bancario"
                ),
                Payment(
                    id = 3,
                    receiptId = 6,
                    subscriberId = 6,
                    amount = 315.50,
                    paymentDate = now - (4L * 86400000L),
                    paymentMethod = "Tarjeta",
                    reference = "TPV-882193",
                    receivedBy = "Caja Principal"
                )
            )
            paymentDao.insertPayments(payments)
        }
    }
}
