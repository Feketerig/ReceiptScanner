package hu.levente.fazekas.receiptscanner.database

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import hu.levente.fazekas.Item
import hu.levente.fazekas.ItemId
import hu.levente.fazekas.LastPrice
import hu.levente.fazekas.Receipt
import hu.levente.fazekas.database.ReceiptDatabase
import hu.levente.fazekas.receiptscanner.database.fake.defaultCategory
import hu.levente.fazekas.receiptscanner.database.fake.sampleCategory
import hu.levente.fazekas.receiptscanner.database.fake.sampleItem
import hu.levente.fazekas.receiptscanner.database.Currency
import hu.levente.fazekas.receiptscanner.database.DateAdapter
import hu.levente.fazekas.receiptscanner.database.ItemCategoryEntity
import hu.levente.fazekas.receiptscanner.database.ItemEntity
import hu.levente.fazekas.receiptscanner.database.SqlDelightItemCategoryRepository
import hu.levente.fazekas.receiptscanner.database.SqlDelightItemRepository
import kotlinx.datetime.Instant
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Properties

class SqlDelightItemRepositoryTest {

    private lateinit var db: ReceiptDatabase
    private lateinit var itemRepository: SqlDelightItemRepository
    private lateinit var categoryRepository: SqlDelightItemCategoryRepository

    @Before
    fun setUp() {
        val driver = JdbcSqliteDriver(
            JdbcSqliteDriver.IN_MEMORY,
            schema = ReceiptDatabase.Schema,
            properties = Properties().apply { put("foreign_keys", "true") })
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
        itemRepository = SqlDelightItemRepository(db)
        categoryRepository = SqlDelightItemCategoryRepository(db)
        //Inserting a default category to have a fallback category when deleting
        categoryRepository.insertCategory(defaultCategory)
    }

    @Test
    fun `Insert item with category successfully`(){
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
        assertEquals(
            LastPrice(sampleItem.itemId, sampleItem.price, sampleItem.unit),
            lastPrice[0]
        )
    }

    @Test
    fun `Insert item with default category successfully`(){
        val itemWithDefaultCategory = sampleItem.copy(
            category = defaultCategory
        )

        itemRepository.insertItem(itemWithDefaultCategory)

        val items = itemRepository.selectAllItem()
        val categories = categoryRepository.selectAllCategory()
        val itemId = db.itemIdQueries.selectAll().executeAsList()
        val lastPrice = db.lastPriceQueries.selectItemLastPrice(sampleItem.itemId).executeAsList()
        assertEquals(1, items.size)
        assertEquals(itemWithDefaultCategory, items[0])
        assertEquals(1, categories.size)
        assertEquals(defaultCategory, categories[0])
        assertEquals(1, itemId.size)
        assertEquals(ItemId(sampleItem.itemId, sampleItem.name), itemId[0])
        assertEquals(1, lastPrice.size)
        assertEquals(
            LastPrice(sampleItem.itemId, sampleItem.price, sampleItem.unit),
            lastPrice[0]
        )
    }

    @Test
    fun `Select an item returns with good formatting`(){
        categoryRepository.insertCategory(sampleCategory)
        itemRepository.insertItem(sampleItem)

        val item = db.itemQueries.selectById(sampleItem.id) { id, itemId, name, quantity, price, unit, categoryId, categoryName, categoryColor, date, currency, receiptId ->
            ItemEntity(
                id = id,
                itemId = itemId,
                name = name,
                quantity = quantity,
                price = price,
                unit = unit,
                category = ItemCategoryEntity(
                    id = categoryId,
                    name = categoryName,
                    color = categoryColor
                ),
                date = date,
                currency = currency,
                receiptId = receiptId
            )
        }.executeAsOne()

        assertEquals(sampleItem, item)
    }

    @Test
    fun `Select all items returns with good formatting and order`(){
        val secondItem = sampleItem.copy(
            id = 2,
            date = Instant.fromEpochSeconds(2)
        )
        categoryRepository.insertCategory(sampleCategory)
        itemRepository.insertItem(sampleItem)
        itemRepository.insertItem(secondItem)

        val items = db.itemQueries.selectAll { id, itemId, name, count, price, unit, categoryId, categoryName, categoryColor, date, currency, receiptId ->
            ItemEntity(
                id = id,
                itemId = itemId,
                name = name,
                quantity = count,
                price = price,
                unit = unit,
                category = ItemCategoryEntity(
                    id = categoryId,
                    name = categoryName,
                    color = categoryColor
                ),
                date = date,
                currency = currency,
                receiptId = receiptId
            )
        }.executeAsList()

        assertEquals(2, items.size)
        assertEquals(secondItem, items[0])
        assertEquals(sampleItem, items[1])
    }

    @Test
    fun `Select items by itemId returns with good formatting and order`(){
        categoryRepository.insertCategory(sampleCategory)
        val secondItem = sampleItem.copy(
            id = 2,
            name = "Alma",
            itemId = 2,
            date = Instant.fromEpochSeconds(2)
        )
        val thirdItem = sampleItem.copy(
            id = 3,
            date = Instant.fromEpochSeconds(3)
        )
        itemRepository.insertItem(sampleItem)
        itemRepository.insertItem(secondItem)
        itemRepository.insertItem(thirdItem)

        val items = db.itemQueries.selectAllByItemId(sampleItem.itemId) { id, itemId, name, quantity, price, unit, categoryId, categoryName, categoryColor, date, currency, receiptId ->
            ItemEntity(
                id = id,
                itemId = itemId,
                name = name,
                quantity = quantity,
                price = price,
                unit = unit,
                category = ItemCategoryEntity(
                    id = categoryId,
                    name = categoryName,
                    color = categoryColor
                ),
                date = date,
                currency = currency,
                receiptId = receiptId
            )
        }.executeAsList()

        assertEquals(2, items.size)
        assertEquals(thirdItem, items[0])
        assertEquals(sampleItem, items[1])
    }

    @Test
    fun `Select items by category returns with good formatting and order`(){
        categoryRepository.insertCategory(sampleCategory)
        val secondCategory = ItemCategoryEntity(
            id = 3,
            name = "Gyümölcs",
            color = null
        )
        categoryRepository.insertCategory(secondCategory)
        val secondItem = sampleItem.copy(
            id = 2,
            category = secondCategory,
            date = Instant.fromEpochSeconds(2)
        )
        val thirdItem = sampleItem.copy(
            id = 3,
            date = Instant.fromEpochSeconds(3)
        )
        itemRepository.insertItem(sampleItem)
        itemRepository.insertItem(secondItem)
        itemRepository.insertItem(thirdItem)

        val items = db.itemQueries.selectAllByCategory(sampleItem.category.id) { id, itemId, name, quantity, price, unit, categoryId, categoryName, categoryColor, date, currency, receiptId ->
            ItemEntity(
                id = id,
                itemId = itemId,
                name = name,
                quantity = quantity,
                price = price,
                unit = unit,
                category = ItemCategoryEntity(
                    id = categoryId,
                    name = categoryName,
                    color = categoryColor
                ),
                date = date,
                currency = currency,
                receiptId = receiptId
            )
        }.executeAsList()

        assertEquals(2, items.size)
        assertEquals(thirdItem, items[0])
        assertEquals(sampleItem, items[1])
    }

    @Test
    fun `Select items by receiptId returns with good formatting and order`(){
        categoryRepository.insertCategory(sampleCategory)
        val secondItem = sampleItem.copy(
            id = 2,
            date = Instant.fromEpochSeconds(2),
            receiptId = 2
        )
        val thirdItem = sampleItem.copy(
            id = 3,
            date = Instant.fromEpochSeconds(3)
        )
        itemRepository.insertItem(sampleItem)
        itemRepository.insertItem(secondItem)
        itemRepository.insertItem(thirdItem)

        val items = db.itemQueries.selectByReceiptId(sampleItem.receiptId) { id, itemId, name, quantity, price, unit, categoryId, categoryName, categoryColor, date, currency, receiptId ->
            ItemEntity(
                id = id,
                itemId = itemId,
                name = name,
                quantity = quantity,
                price = price,
                unit = unit,
                category = ItemCategoryEntity(
                    id = categoryId,
                    name = categoryName,
                    color = categoryColor
                ),
                date = date,
                currency = currency,
                receiptId = receiptId
            )
        }.executeAsList()

        assertEquals(2, items.size)
        assertEquals(thirdItem, items[0])
        assertEquals(sampleItem, items[1])
    }

    @Test
    fun `Update item name`(){
        categoryRepository.insertCategory(sampleCategory)
        itemRepository.insertItem(sampleItem)
        val newItem = ItemEntity(
            id = 1,
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

        itemRepository.updateItem(newItem)

        val updatedItems = itemRepository.selectAllItem()
        val updatedCategories = categoryRepository.selectAllCategory()
        val updatedItemId = db.itemIdQueries.selectAll().executeAsList()
        val updatedLastPrice = db.lastPriceQueries.selectItemLastPrice(newItem.itemId).executeAsList()
        assertEquals(1, updatedItems.size)
        assertEquals(newItem, updatedItems[0])
        assertEquals(2, updatedCategories.size)
        assertEquals(sampleCategory, updatedCategories[1])
        assertEquals(1, updatedItemId.size)
        assertEquals(ItemId(newItem.itemId, newItem.name), updatedItemId[0])
        assertEquals(1, updatedLastPrice.size)
        assertEquals(LastPrice(newItem.itemId, newItem.price, newItem.unit), updatedLastPrice[0])
    }

    @Test
    fun `Update item's category`(){
        categoryRepository.insertCategory(sampleCategory)
        itemRepository.insertItem(sampleItem)
        val newCategory = ItemCategoryEntity(
            id = 2,
            name = "Gyümölcs",
            color = null
        )
        val newItem = sampleItem.copy(
            category = newCategory
        )

        itemRepository.updateItem(newItem)

        val updatedItems = itemRepository.selectAllItem()
        val updatedCategories = categoryRepository.selectAllCategory()
        val updatedItemId = db.itemIdQueries.selectAll().executeAsList()
        val updatedLastPrice = db.lastPriceQueries.selectItemLastPrice(newItem.itemId).executeAsList()
        assertEquals(1, updatedItems.size)
        assertEquals(newItem, updatedItems[0])
        assertEquals(2, updatedCategories.size)
        assertEquals(newCategory, updatedCategories[1])
        assertEquals(1, updatedItemId.size)
        assertEquals(ItemId(newItem.itemId, newItem.name), updatedItemId[0])
        assertEquals(1, updatedLastPrice.size)
        assertEquals(LastPrice(newItem.itemId, newItem.price, newItem.unit), updatedLastPrice[0])
    }

    @Test
    fun `Delete category, item's category set to default`(){
        categoryRepository.insertCategory(sampleCategory)
        itemRepository.insertItem(sampleItem)
        val expectedItem = sampleItem.copy(
            category = defaultCategory
        )

        categoryRepository.deleteCategory(sampleItem.category.id)

        val updatedItems = itemRepository.selectAllItem()
        val updatedCategories = categoryRepository.selectAllCategory()
        val updatedItemId = db.itemIdQueries.selectAll().executeAsList()
        val updatedLastPrice = db.lastPriceQueries.selectItemLastPrice(sampleItem.itemId).executeAsList()
        assertEquals(1, updatedItems.size)
        assertEquals(expectedItem, updatedItems[0])
        assertEquals(1, updatedCategories.size)
        assertEquals(defaultCategory, updatedCategories[0])
        assertEquals(1, updatedItemId.size)
        assertEquals(ItemId(sampleItem.itemId, sampleItem.name), updatedItemId[0])
        assertEquals(1, updatedLastPrice.size)
        assertEquals(LastPrice(sampleItem.itemId, sampleItem.price, sampleItem.unit), updatedLastPrice[0])
    }

    @Test
    fun `Delete item, category not deleted, itemId deleted, lastPrice deleted`(){
        categoryRepository.insertCategory(sampleCategory)
        itemRepository.insertItem(sampleItem)

        itemRepository.deleteItem(sampleItem.id)

        val deletedItems = itemRepository.selectAllItem()
        val deletedCategories = categoryRepository.selectAllCategory()
        val deletedItemId = db.itemIdQueries.selectAll().executeAsList()
        val deletedPrice = db.lastPriceQueries.selectItemLastPrice(sampleItem.itemId).executeAsList()
        assertEquals(0, deletedItems.size)
        assertEquals(2, deletedCategories.size)
        assertEquals(defaultCategory, deletedCategories[0])
        assertEquals(sampleCategory, deletedCategories[1])
        assertEquals(0, deletedItemId.size)
        assertEquals(0, deletedPrice.size)
    }
}