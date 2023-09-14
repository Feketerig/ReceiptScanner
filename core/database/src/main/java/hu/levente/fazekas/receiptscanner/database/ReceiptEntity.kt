package hu.levente.fazekas.receiptscanner.database

import kotlinx.datetime.Instant

data class ReceiptEntity(
    val id: Long,
    val name: String,
    val date: Instant,
    val currency: Currency,
    val sumOfPrice: Long,
    val description: String,
    val imageUri: String,
    val items: List<ItemEntity>
)