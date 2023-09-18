package hu.levente.fazekas.database

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import hu.levente.fazekas.Item
import hu.levente.fazekas.ItemId
import hu.levente.fazekas.LastPrice
import hu.levente.fazekas.Receipt
import hu.levente.fazekas.database.fake.defaultCategory
import hu.levente.fazekas.database.fake.sampleCategory
import hu.levente.fazekas.database.fake.sampleItem
import hu.levente.fazekas.database.fake.sampleTag
import hu.levente.fazekas.receiptscanner.database.Currency
import hu.levente.fazekas.receiptscanner.database.DateAdapter
import hu.levente.fazekas.receiptscanner.database.ItemCategoryEntity
import hu.levente.fazekas.receiptscanner.database.ItemEntity
import hu.levente.fazekas.receiptscanner.database.SqlDelightItemCategoryRepository
import hu.levente.fazekas.receiptscanner.database.SqlDelightItemRepository
import hu.levente.fazekas.receiptscanner.database.SqlDelightTagRepository
import hu.levente.fazekas.receiptscanner.database.TagEntity
import kotlinx.datetime.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.util.Properties

class DatabaseTest {

    private lateinit var db: ReceiptDatabase

    @Before
    fun before() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY, schema = ReceiptDatabase.Schema, properties = Properties().apply { put("foreign_keys", "true") })
        db = ReceiptDatabase(
            driver = driver,
            ReceiptAdapter = Receipt.Adapter(
                dateAdapter = DateAdapter(),
                currencyAdapter = EnumColumnAdapter()
            ),
            ItemAdapter = Item.Adapter(
                dateAdapter = DateAdapter(),
                currencyAdapter = EnumColumnAdapter()
            )
        )
    }

    @Test
    fun itemCategoryRepositoryTest(){
        val itemCategoryRepository = SqlDelightItemCategoryRepository(db)

        //insert default category
        itemCategoryRepository.insertCategory(defaultCategory)

        //insert
        itemCategoryRepository.insertCategory(sampleCategory)

        val categories = itemCategoryRepository.selectAllCategory()

        assertEquals(2, categories.size)
        assertEquals(defaultCategory, categories[0])
        assertEquals(sampleCategory, categories[1])

        //update
        val newCategory = ItemCategoryEntity(2, "Gyümölcs", null)
        itemCategoryRepository.updateCategory(newCategory)

        val updatedCategories = itemCategoryRepository.selectAllCategory()

        assertEquals(2, updatedCategories.size)
        assertEquals(defaultCategory, updatedCategories[0])
        assertEquals(newCategory, updatedCategories[1])

        //delete
        itemCategoryRepository.deleteCategory(sampleCategory.id)

        val deletedCategories = itemCategoryRepository.selectAllCategory()

        assertEquals(1, deletedCategories.size)
        assertEquals(defaultCategory, deletedCategories[0])

        //insert same name twice throws exception
        itemCategoryRepository.insertCategory(sampleCategory)

        val exception = assertThrows(Exception::class.java) {
            itemCategoryRepository.insertCategory(sampleCategory)
        }

        assertEquals("[SQLITE_CONSTRAINT_UNIQUE] A UNIQUE constraint failed (UNIQUE constraint failed: ItemCategory.name)", exception.message)
    }

    @Test
    fun  itemRepositoryTest(){
        val itemRepository = SqlDelightItemRepository(db)
        val categoryRepository = SqlDelightItemCategoryRepository(db)

        //insert default category
        categoryRepository.insertCategory(defaultCategory)

        //insert
        categoryRepository.insertCategory(sampleCategory)
        itemRepository.insertItem(sampleItem)

        val items = itemRepository.selectAllItem()
        val categories = categoryRepository.selectAllCategory()
        val itemId = db.itemIdQueries.selectAll().executeAsList()
        val lastPrice = db.lastPriceQueries.selectItemLastPrice(sampleItem.itemId).executeAsList()

        assertEquals(1, items.size)
        assertEquals(sampleItem, items[0])
        assertEquals(2, categories.size)
        assertEquals(sampleCategory, categories[1])
        assertEquals(1, itemId.size)
        assertEquals(ItemId(sampleItem.itemId, sampleItem.name), itemId[0])
        assertEquals(1, lastPrice.size)
        assertEquals(LastPrice(sampleItem.itemId, sampleItem.price, sampleItem.unit), lastPrice[0])

        //update
        val newCategory = ItemCategoryEntity(
            id = 2,
            name = "Gyümölcs",
            color = null
        )
        val newItem = ItemEntity(
            id = 1,
            itemId = 2,
            name = "Sajt",
            quantity = 2,
            price = 793.0,
            unit = "kg",
            category = newCategory,
            date = Instant.fromEpochSeconds(1),
            currency = Currency.HUF,
            receiptId = 1
        )
        itemRepository.updateItem(newItem)

        val upgradedItems = itemRepository.selectAllItem()
        val upgradedCategories = categoryRepository.selectAllCategory()
        val upgradedItemId = db.itemIdQueries.selectAll().executeAsList()
        val upgradedLastPrice = db.lastPriceQueries.selectItemLastPrice(newItem.itemId).executeAsList()

        assertEquals(1, upgradedItems.size)
        assertEquals(newItem, upgradedItems[0])
        assertEquals(2, upgradedCategories.size)
        assertEquals(newCategory, upgradedCategories[1])
        assertEquals(1, upgradedItemId.size)
        assertEquals(ItemId(newItem.itemId, newItem.name), upgradedItemId[0])
        assertEquals(1, upgradedLastPrice.size)
        assertEquals(LastPrice(newItem.itemId, newItem.price, newItem.unit), upgradedLastPrice[0])

        //Change category color and item name
        val newColorCategory = ItemCategoryEntity(
            id = 2,
            name = "Gyümölcs",
            color = 100
        )
        val newColorItem = ItemEntity(
            id = 1,
            itemId = 2,
            name = "Alma",
            quantity = 2,
            price = 793.0,
            unit = "kg",
            category = newColorCategory,
            date = Instant.fromEpochSeconds(1),
            currency = Currency.HUF,
            receiptId = 1
        )
        itemRepository.updateItem(newColorItem)

        val upgradedColorItems = itemRepository.selectAllItem()
        val upgradedColorCategories = categoryRepository.selectAllCategory()
        val upgradedColorItemId = db.itemIdQueries.selectAll().executeAsList()
        val upgradedColorLastPrice = db.lastPriceQueries.selectItemLastPrice(newColorItem.itemId).executeAsList()

        assertEquals(1, upgradedColorItems.size)
        assertEquals(newColorItem, upgradedColorItems[0])
        assertEquals(2, upgradedColorCategories.size)
        assertEquals(newColorCategory, upgradedColorCategories[1])
        assertEquals(1, upgradedColorItemId.size)
        assertEquals(ItemId(newColorItem.itemId, newColorItem.name), upgradedColorItemId[0])
        assertEquals(1, upgradedColorLastPrice.size)
        assertEquals(LastPrice(newColorItem.itemId, newColorItem.price, newColorItem.unit), upgradedColorLastPrice[0])

        //Delete Category
        categoryRepository.deleteCategory(newColorCategory.id)

        val deletedCategoryItems = itemRepository.selectAllItem()
        val deletedCategoryCategories = categoryRepository.selectAllCategory()
        val deletedCategoryItemId = db.itemIdQueries.selectAll().executeAsList()
        val deletedCategoryPrice = db.lastPriceQueries.selectItemLastPrice(newColorItem.itemId).executeAsList()

        assertEquals(1, deletedCategoryItems.size)
        assertEquals(1, deletedCategoryCategories.size)
        assertEquals(defaultCategory, deletedCategoryCategories[0])
        assertEquals(1, deletedCategoryItemId.size)
        assertEquals(1, deletedCategoryPrice.size)

        //Delete item
        itemRepository.deleteItem(newColorItem.id)

        val deletedItems = itemRepository.selectAllItem()
        val deletedCategories = categoryRepository.selectAllCategory()
        val deletedItemId = db.itemIdQueries.selectAll().executeAsList()
        val deletedPrice = db.lastPriceQueries.selectItemLastPrice(newColorItem.itemId).executeAsList()

        assertEquals(0, deletedItems.size)
        assertEquals(1, deletedCategories.size)
        assertEquals(defaultCategory, deletedCategoryCategories[0])
        assertEquals(0, deletedItemId.size)
        assertEquals(0, deletedPrice.size)
    }
}