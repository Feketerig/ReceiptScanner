package hu.levente.fazekas.receiptscanner.database

import kotlinx.datetime.Instant

data class ReducedReceiptEntity(
    val id: Long,
    val name: String,
    val date: Instant,
    val currency: Currency,
    val sumOfPrice: Long,
    val tags: List<TagEntity>
)