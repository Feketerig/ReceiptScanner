package hu.levente.fazekas.receiptscanner.database

import hu.levente.fazekas.currency.Currency
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class ReceiptEntity(
    val id: Long = 0,
    val name: String = "",
    val date: Instant = Clock.System.now(),
    val currency: Currency = Currency.HUF,
    val sumOfPrice: Long = 0,
    val description: String = "",
    val imageUri: String? = null,
    val tags: List<TagEntity> = emptyList(),
    val items: List<ItemEntity> = emptyList()
)