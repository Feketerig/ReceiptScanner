package hu.levente.fazekas.receiptscanner.database

import hu.levente.fazekas.ItemCategory
import kotlinx.datetime.Instant

data class ItemEntity(
    val id: Long,
    val itemId : Long,
    val name: String,
    val count: Long, //quantity
    val price: Double, //price per measurement unit
    val unit: String, //measurement unit
    val category: ItemCategory,
    val date: Instant,
    val currency: Currency
)