package hu.levente.fazekas.database.fake

import hu.levente.fazekas.receiptscanner.database.Currency
import hu.levente.fazekas.receiptscanner.database.ItemCategoryEntity
import hu.levente.fazekas.receiptscanner.database.ItemEntity
import hu.levente.fazekas.receiptscanner.database.ReceiptEntity
import hu.levente.fazekas.receiptscanner.database.TagEntity
import kotlinx.datetime.Instant

val defaultCategory = ItemCategoryEntity(
    id = 1,
    name = "Not Specified",
    color = 111
)

val sampleCategory = ItemCategoryEntity(
    id = 2,
    name = "Tejtermék",
    color = 789
)

val sampleItem = ItemEntity(
    id = 1,
    itemId = 1,
    name = "Tej",
    quantity = 3,
    price = 398.0,
    unit = "L",
    category = sampleCategory,
    date = Instant.fromEpochSeconds(1),
    currency = Currency.HUF,
    receiptId = 1
)

val sampleItems = listOf(
    ItemEntity(
        id = 1,
        itemId = 1,
        name = "Tej",
        quantity = 3,
        price = 398.0,
        unit = "L",
        category = sampleCategory,
        date = Instant.fromEpochSeconds(1),
        currency = Currency.HUF,
        receiptId = 1
    ),
    ItemEntity(
        id = 2,
        itemId = 1,
        name = "Tej",
        quantity = 5,
        price = 468.0,
        unit = "L",
        category = sampleCategory,
        date = Instant.fromEpochSeconds(2),
        currency = Currency.HUF,
        receiptId = 1
    ),
    ItemEntity(
        id = 3,
        itemId = 2,
        name = "Sajt",
        quantity = 2,
        price = 793.0,
        unit = "kg",
        category = sampleCategory,
        date = Instant.fromEpochSeconds(1),
        currency = Currency.HUF,
        receiptId = 1
    )
)

val sampleTag = TagEntity(
    id = 1,
    name = "Auchan"
)

val sampleReceipt = ReceiptEntity(
    id = 1,
    name = "Auchan",
    date = Instant.fromEpochSeconds(1),
    currency = Currency.HUF,
    sumOfPrice = 5987,
    description = "Egy példa blokk",
    imageUri = "",
    items = sampleItems
)

