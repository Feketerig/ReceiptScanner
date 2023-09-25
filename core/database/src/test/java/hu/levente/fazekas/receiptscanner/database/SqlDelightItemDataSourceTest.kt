package hu.levente.fazekas.receiptscanner.database

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import hu.levente.fazekas.Item
import hu.levente.fazekas.ItemId
import hu.levente.fazekas.LastPrice
import hu.levente.fazekas.Receipt
import hu.levente.fazekas.database.ReceiptDatabase
import hu.levente.fazekas.receiptscanner.database.fake.defaultCategory
import hu.levente.fazekas.receiptscanner.database.fake.sampleCategory
import hu.levente.fazekas.receiptscanner.database.fake.sampleItem
import hu.levente.fazekas.receiptscanner.database.fake.sampleItems
import kotlinx.datetime.Instant
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Properties

class SqlDelightItemDataSourceTest {

    private lateinit var db: ReceiptDatabase
    private lateinit var itemRepository: SqlDelightItemDataSource
    private lateinit var categoryRepository: SqlDelightItemCategoryDataSource

    @BeforeEach
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
        itemRepository = SqlDelightItemDataSource(db)
        categoryRepository = SqlDelightItemCategoryDataSource(db)
        //Inserting a default category to have a fallback category when deleting
        categoryRepository.insertCategory(defaultCategory)
        categoryRepository.insertCategory(sampleCategory)
        //Inserting a receipt for the foreign key constrain
        db.receiptQueries.insert(name = "", date = Instant.fromEpochSeconds(1), currency = Currency.HUF, sumOfPrice = 1, description = null, imageUri = "")
    }

    @Test
    fun `Insert item with category successfully`(){
        itemRepository.insertItem(sampleItem)

        val items = itemRepository.selectAll()
        val categories = categoryRepository.selectAllCategory()
        val itemIds = db.itemIdQueries.selectAll().executeAsList()
        val lastPrices = db.lastPriceQueries.selectLastPriceByItemId(sampleItem.itemId).executeAsList()
        assertThat(items).containsExactly(sampleItem)
        assertThat(categories).containsExactly(defaultCategory, sampleCategory)
        assertThat(itemIds).containsExactly(ItemId(sampleItem.itemId, sampleItem.name))
        assertThat(lastPrices).containsExactly(LastPrice(sampleItem.itemId, sampleItem.price, sampleItem.unit))
    }

    @Test
    fun `Select an item returns with good formatting`(){
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

        assertThat(item).isEqualTo(sampleItem)
    }

    @Test
    fun `Select all items returns with good formatting and order`(){
        val secondItem = sampleItem.copy(
            id = 2,
            date = Instant.fromEpochSeconds(2)
        )
        itemRepository.insertItem(sampleItem)
        itemRepository.insertItem(secondItem)

        val items = db.itemQueries.selectAll { id, itemId, name, quantity, price, unit, categoryId, categoryName, categoryColor, date, currency, receiptId ->
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

        assertThat(items).containsExactly(secondItem, sampleItem)
    }

    @Test
    fun `Select items by itemId returns with good formatting and order`(){
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

        assertThat(items).containsExactly(thirdItem, sampleItem)
    }

    @Test
    fun `Insert multiple items successfully`(){
        itemRepository.insertItem(sampleItems[0])
        itemRepository.insertItem(sampleItems[1])
        itemRepository.insertItem(sampleItems[2])

        val items = itemRepository.selectAll()
        val categories = categoryRepository.selectAllCategory()
        val itemIds = db.itemIdQueries.selectAll().executeAsList()
        val lastPriceForFirstItem = db.lastPriceQueries.selectLastPriceByItemId(sampleItems[0].itemId).executeAsList()
        val lastPriceForThirdItem = db.lastPriceQueries.selectLastPriceByItemId(sampleItems[2].itemId).executeAsList()
        assertThat(items).containsExactly(sampleItems[0], sampleItems[1], sampleItems[2])
        assertThat(categories).containsExactly(defaultCategory, sampleCategory)
        assertThat(itemIds).containsExactly(ItemId(sampleItems[0].itemId, sampleItems[0].name), ItemId(sampleItems[2].itemId, sampleItems[2].name))
        assertThat(lastPriceForFirstItem).containsExactly(LastPrice(sampleItems[1].itemId, sampleItems[1].price, sampleItems[1].unit))
        assertThat(lastPriceForThirdItem).containsExactly(LastPrice(sampleItems[2].itemId, sampleItems[2].price, sampleItems[2].unit))
    }

    @Test
    fun `Select items by category returns with good formatting and order`(){
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

        assertThat(items).containsExactly(thirdItem, sampleItem)
    }

    @Test
    fun `Select items by receiptId returns with good formatting and order`(){
        //inserting a fake receipt for foreign key constrains
        db.receiptQueries.insert(name = "", date = Instant.fromEpochSeconds(1), currency = Currency.HUF, sumOfPrice = 1, description = null, imageUri = "")
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

        assertThat(items).containsExactly(thirdItem, sampleItem)
    }

    @Test
    fun `Update item name`(){
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

        val items = itemRepository.selectAll()
        val categories = categoryRepository.selectAllCategory()
        val itemIds = db.itemIdQueries.selectAll().executeAsList()
        val lastPrices = db.lastPriceQueries.selectLastPriceByItemId(newItem.itemId).executeAsList()
        assertThat(items).containsExactly(newItem)
        assertThat(categories).containsExactly(defaultCategory, sampleCategory)
        assertThat(itemIds).containsExactly(ItemId(newItem.itemId, newItem.name))
        assertThat(lastPrices).containsExactly(LastPrice(newItem.itemId, newItem.price, newItem.unit))
    }

    @Test
    fun `Update item's category`(){
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

        val items = itemRepository.selectAll()
        val categories = categoryRepository.selectAllCategory()
        val itemIds = db.itemIdQueries.selectAll().executeAsList()
        val lastPrices = db.lastPriceQueries.selectLastPriceByItemId(newItem.itemId).executeAsList()
        assertThat(items).containsExactly(newItem)
        assertThat(categories).containsExactly(defaultCategory, newCategory)
        assertThat(itemIds).containsExactly(ItemId(newItem.itemId, newItem.name))
        assertThat(lastPrices).containsExactly(LastPrice(newItem.itemId, newItem.price, newItem.unit))
    }

    @Test
    fun `Delete category, item's category set to default`(){
        itemRepository.insertItem(sampleItem)
        val expectedItem = sampleItem.copy(
            category = defaultCategory
        )

        categoryRepository.deleteCategory(sampleItem.category.id)

        val items = itemRepository.selectAll()
        val categories = categoryRepository.selectAllCategory()
        val itemIds = db.itemIdQueries.selectAll().executeAsList()
        val lastPrices = db.lastPriceQueries.selectLastPriceByItemId(sampleItem.itemId).executeAsList()
        assertThat(items).containsExactly(expectedItem)
        assertThat(categories).containsExactly(defaultCategory)
        assertThat(itemIds).containsExactly(ItemId(sampleItem.itemId, sampleItem.name))
        assertThat(lastPrices).containsExactly(LastPrice(sampleItem.itemId, sampleItem.price, sampleItem.unit))
    }

    @Test
    fun `Delete item, category not deleted, itemId deleted, lastPrice deleted`(){
        itemRepository.insertItem(sampleItem)

        itemRepository.deleteItem(sampleItem.id)

        val items = itemRepository.selectAll()
        val categories = categoryRepository.selectAllCategory()
        val itemIds = db.itemIdQueries.selectAll().executeAsList()
        val lastPrices = db.lastPriceQueries.selectLastPriceByItemId(sampleItem.itemId).executeAsList()
        assertThat(items).isEmpty()
        assertThat(categories).containsExactly(defaultCategory, sampleCategory)
        assertThat(itemIds).isEmpty()
        assertThat(lastPrices).isEmpty()
    }
}