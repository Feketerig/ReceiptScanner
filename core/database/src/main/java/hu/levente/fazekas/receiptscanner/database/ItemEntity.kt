package hu.levente.fazekas.receiptscanner.database

import hu.levente.fazekas.currency.Currency
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class ItemEntity(
    val id: Long = 0,
    val itemId : Long = 0,
    val name: String = "",
    val quantity: Long = 0,
    val price: Double = 0.0, //price per measurement unit
    val unit: String = "", //measurement unit
    val category: ItemCategoryEntity = ItemCategoryEntity(0, "", 0),
    val date: Instant = Clock.System.now(),
    val currency: Currency = Currency.HUF,
    val receiptId: Long = 0
)