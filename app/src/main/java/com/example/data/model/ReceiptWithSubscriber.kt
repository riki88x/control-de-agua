package com.example.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class ReceiptWithSubscriber(
    @Embedded val receipt: Receipt,
    @Relation(
        parentColumn = "subscriberId",
        entityColumn = "id"
    )
    val subscriber: Subscriber
)
