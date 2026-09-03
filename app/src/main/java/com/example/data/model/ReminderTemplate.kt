package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ReminderType(val title: String, val description: String) {
    UPCOMING(
        "Aviso Preventivo",
        "Recordatorio de pago próximo a vencer con fecha límite y monto."
    ),
    OVERDUE(
        "Aviso de Cobro Vencido",
        "Notificación formal de factura vencida y solicitud de regularización para evitar corte."
    ),
    PAYMENT_CONFIRMATION(
        "Comprobante de Pago",
        "Agradecimiento y confirmación de pago recibido con folio."
    ),
    FINAL_NOTICE(
        "Aviso de Suspensión",
        "Último aviso con orden de suspensión de suministro de agua potable."
    )
}

object ReminderTemplateHelper {
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun generateMessage(
        type: ReminderType,
        subscriber: Subscriber,
        receipt: Receipt,
        waterServiceOrgName: String = "Servicio de Agua Potable"
    ): String {
        val dueDateStr = dateFormat.format(Date(receipt.dueDate))
        val pendingStr = String.format(Locale.US, "$%.2f", receipt.pendingBalance)

        return when (type) {
            ReminderType.UPCOMING -> {
                """
                💧 *${waterServiceOrgName}* - Aviso de Cobranza
                Estimado(a) *${subscriber.name}*, le recordamos que su recibo de agua potable correspondiente al período *${receipt.period}* está próximo a vencer.
                
                📋 *Recibo:* ${receipt.receiptNumber}
                🔢 *Cuenta / Medidor:* ${subscriber.accountNumber} / Medidor: ${subscriber.meterNumber}
                💰 *Total a pagar:* ${pendingStr}
                📅 *Fecha límite:* ${dueDateStr}
                
                Evite recargos realizando su pago en oficinas o por transferencia. ¡Gracias por su puntualidad!
                """.trimIndent()
            }
            ReminderType.OVERDUE -> {
                """
                ⚠️ *${waterServiceOrgName}* - Aviso de Saldo Vencido
                Estimado(a) *${subscriber.name}*, le informamos que su recibo de agua potable del período *${receipt.period}* presenta saldo vencido.
                
                📋 *Recibo:* ${receipt.receiptNumber}
                👤 *Titular:* ${subscriber.name}
                🏠 *Dirección:* ${subscriber.address}
                💰 *Saldo pendiente:* ${pendingStr}
                📅 *Venció el:* ${dueDateStr}
                
                Le solicitamos acudir a liquidar o reportar su pago a la brevedad para evitar la suspensión temporal del servicio. Agradecemos su atención.
                """.trimIndent()
            }
            ReminderType.PAYMENT_CONFIRMATION -> {
                """
                ✅ *${waterServiceOrgName}* - Confirmación de Pago
                Estimado(a) *${subscriber.name}*, hemos registrado su pago satisfactoriamente.
                
                📋 *Recibo:* ${receipt.receiptNumber}
                💰 *Monto abonado:* ${String.format(Locale.US, "$%.2f", receipt.paidAmount)}
                🗓️ *Período:* ${receipt.period}
                
                Muchas gracias por mantener su servicio al día. ¡Cuidemos el agua!
                """.trimIndent()
            }
            ReminderType.FINAL_NOTICE -> {
                """
                🚨 *${waterServiceOrgName}* - Notificación Urgente de Corte
                Sr.(a) *${subscriber.name}*, su cuenta *${subscriber.accountNumber}* registra adeudo vencido de *${pendingStr}*.
                
                📋 *Recibo:* ${receipt.receiptNumber}
                Medidor: ${subscriber.meterNumber}
                
                Se ha programado la suspensión del suministro de agua. Por favor acuda hoy mismo a oficinas centrales a regularizar su cuenta para evitar costos por reconexión.
                """.trimIndent()
            }
        }
    }
}
