package hu.levente.fazekas.database

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import hu.levente.fazekas.Item
import hu.levente.fazekas.ItemCategory
import hu.levente.fazekas.ItemId
import hu.levente.fazekas.Receipt
import hu.levente.fazekas.SelectAll
import hu.levente.fazekas.SelectAllById
import hu.levente.fazekas.receiptscanner.database.DateAdapter
import kotlinx.datetime.Instant
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DatabaseTest {

    private lateinit var db: ReceiptDatabase

    @Before
    fun before() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY, schema = ReceiptDatabase.Schema)
        db = ReceiptDatabase(
            driver = driver,
            ReceiptAdapter = Receipt.Adapter(
                dateAdapter = DateAdapter(),
                currencyAdapter = EnumColumnAdapter()
            ),
            ItemAdapter = Item.Adapter(
                dateAdapter = DateAdapter()
            )
        )
    }

    @Test
    fun insertItem() {
        db.itemQueries.insert("Tej", "Tejtermék", 5L,2L, 100.0, "L", Instant.fromEpochSeconds(1))

        val items = db.itemQueries.selectAll().executeAsList()
        val itemIds = db.itemIdQueries.selectAll().executeAsList()
        val categoryIds = db.itemCategoryQueries.selectAll().executeAsList()
        assertEquals(items.size, 1)
        assertEquals(itemIds.size, 1)
        assertEquals(categoryIds.size, 1)
        assertEquals(items[0], SelectAll(1, 1, "Tej", 2L, 100.0, "L", "Tejtermék"))
        assertEquals(itemIds[0], ItemId(1, "Tej"))
        assertEquals(categoryIds[0], ItemCategory(1, "Tejtermék", 5L))
    }

    @Test
    fun selectAllItemById() {
        db.itemQueries.insert("Tej", "Tejtermék", 5L,2L, 100.0, "L", Instant.fromEpochSeconds(1))
        db.itemQueries.insert("Tej", "Tejtermék", 5L,5L, 120.0, "L", Instant.fromEpochSeconds(2))

        val items = db.itemQueries.selectAllById(1).executeAsList()
        val itemIds = db.itemIdQueries.selectAll().executeAsList()
        val categoryIds = db.itemCategoryQueries.selectAll().executeAsList()
        assertEquals(items.size, 2)
        assertEquals(itemIds.size, 1)
        assertEquals(categoryIds.size, 1)
        assertEquals(items[0], SelectAllById(1, 1, "Tej", 2L, 100.0, "L", "Tejtermék"))
        assertEquals(items[1], SelectAllById(2, 1, "Tej", 5L, 120.0, "L", "Tejtermék"))
        assertEquals(itemIds[0], ItemId(1, "Tej"))
        assertEquals(categoryIds[0], ItemCategory(1, "Tejtermék", 5L))
    }
}