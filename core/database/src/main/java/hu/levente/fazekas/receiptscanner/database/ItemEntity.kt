package hu.levente.fazekas.receiptscanner.database

import hu.levente.fazekas.currency.Currency
import kotlinx.datetime.Instant

data class ItemEntity(
    val id: Long,
    val itemId : Long,
    val name: String,
    val quantity: Long,
    val price: Double, //price per measurement unit
    val unit: String, //measurement unit
    val category: ItemCategoryEntity,
    val date: Instant,
    val currency: Currency,
    val receiptId: Long
)