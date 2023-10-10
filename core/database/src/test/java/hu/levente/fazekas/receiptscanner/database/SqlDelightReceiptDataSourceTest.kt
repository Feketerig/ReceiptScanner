package hu.levente.fazekas.receiptscanner.database

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import hu.levente.fazekas.Item
import hu.levente.fazekas.Receipt
import hu.levente.fazekas.database.ReceiptDatabase
import hu.levente.fazekas.receiptscanner.database.fake.defaultCategory
import hu.levente.fazekas.receiptscanner.database.fake.sampleCategory
import hu.levente.fazekas.receiptscanner.database.fake.sampleItems
import hu.levente.fazekas.receiptscanner.database.fake.sampleReceipt
import hu.levente.fazekas.receiptscanner.database.fake.sampleTag
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Properties

class SqlDelightReceiptDataSourceTest {

    private lateinit var db: ReceiptDatabase
    private lateinit var itemRepository: SqlDelightItemDataSource
    private lateinit var categoryRepository: SqlDelightItemCategoryDataSource
    private lateinit var receiptRepository: SqlDelightReceiptDataSource
    private lateinit var tagRepository: SqlDelightTagDataSource

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
        receiptRepository = SqlDelightReceiptDataSource(db, itemRepository)
        tagRepository = SqlDelightTagDataSource(db)
    }

    @Test
    fun `Insert Receipt with no tags successfully`() = runTest {
        val receiptWithNoTags = sampleReceipt.copy(tags = emptyList())
        receiptRepository.insertReceipt(receiptWithNoTags)

        val receipt = receiptRepository.selectReceiptById(sampleReceipt.id)
        val items = itemRepository.selectAll()
        val tags = tagRepository.selectByReceiptId(receipt.id).first()
        assertThat(receipt).isEqualTo(receiptWithNoTags)
        assertThat(items).containsExactly(*sampleItems.toTypedArray())
        assertThat(tags).isEmpty()
    }

    @Test
    fun `Insert Receipt with tags successfully`() = runTest {
        receiptRepository.insertReceipt(sampleReceipt)

        val receipt = receiptRepository.selectReceiptById(sampleReceipt.id)
        val items = itemRepository.selectAll()
        val tags = tagRepository.selectByReceiptId(receipt.id).first()
        assertThat(receipt).isEqualTo(sampleReceipt)
        assertThat(items).containsExactly(*sampleItems.toTypedArray())
        assertThat(tags).containsExactly(sampleTag)
    }

    @Test
    fun `Insert Receipt with multiple tags successfully`() = runTest {
        val newTag = TagEntity(2, "Aldi")
        val receiptWithMultipleTags = sampleReceipt.copy(
            tags = listOf(sampleTag, newTag)
        )
        receiptRepository.insertReceipt(receiptWithMultipleTags)

        val receipt = receiptRepository.selectReceiptById(receiptWithMultipleTags.id)
        val items = itemRepository.selectAll()
        val tags = tagRepository.selectByReceiptId(receipt.id).first()
        assertThat(receipt).isEqualTo(receiptWithMultipleTags)
        assertThat(items).containsExactly(*sampleItems.toTypedArray())
        assertThat(tags).containsExactly(sampleTag, newTag)
    }

    @Test
    fun `Select all receipt without items`(){
        val secondReceipt = sampleReceipt.copy(
            id = 2,
            name = "Aldi",
            date = Instant.fromEpochSeconds(2),
            //tags = emptyList(), //TODO fix receipt with no tag
        )
        receiptRepository.insertReceipt(sampleReceipt)
        receiptRepository.insertReceipt(secondReceipt)

        val receipts = receiptRepository.selectAllReducedReceipt("")
        assertThat(receipts).containsExactly(
            ReducedReceiptEntity(
                id = secondReceipt.id,
                name = secondReceipt.name,
                date = secondReceipt.date,
                currency = secondReceipt.currency,
                sumOfPrice = secondReceipt.sumOfPrice,
                tags = secondReceipt.tags
            ),
            ReducedReceiptEntity(
                id = sampleReceipt.id,
                name = sampleReceipt.name,
                date = sampleReceipt.date,
                currency = sampleReceipt.currency,
                sumOfPrice = sampleReceipt.sumOfPrice,
                tags = sampleReceipt.tags
            )
        )
    }

    @Test
    fun `Delete receipt, receiptRepository and tagRepository becomes empty`() = runBlocking {
        receiptRepository.insertReceipt(sampleReceipt)

        receiptRepository.deleteReceipt(sampleReceipt.id)

        val receipts = receiptRepository.selectAllReducedReceipt("")
        val tags = tagRepository.selectAll().first()
        assertThat(receipts).isEmpty()
        assertThat(tags).isEmpty()
    }

    @Test
    fun `Delete receipt with extra tag, receiptRepository becomes empty`() = runBlocking {
        receiptRepository.insertReceipt(sampleReceipt)
        tagRepository.insert("NewTag")

        receiptRepository.deleteReceipt(sampleReceipt.id)

        val receipts = receiptRepository.selectAllReducedReceipt("")
        val tags = tagRepository.selectAll().first()
        assertThat(receipts).isEmpty()
        assertThat(tags).containsExactly(TagEntity(2, "NewTag"))
    }

    @Test
    fun `Delete receipt with extra receipt, tagRepository becomes empty`() = runBlocking {
        val secondReceipt = sampleReceipt.copy(
            id = 2,
            name = "Aldi",
            date = Instant.fromEpochSeconds(2)
        )
        val secondReceiptReduced = ReducedReceiptEntity(
            id = secondReceipt.id,
            name = secondReceipt.name,
            date = secondReceipt.date,
            currency = secondReceipt.currency,
            sumOfPrice = secondReceipt.sumOfPrice,
            tags = secondReceipt.tags
        )
        receiptRepository.insertReceipt(sampleReceipt)
        receiptRepository.insertReceipt(secondReceipt)


        receiptRepository.deleteReceipt(sampleReceipt.id)

        val receipts = receiptRepository.selectAllReducedReceipt("")
        val tags = tagRepository.selectAll().first()
        assertThat(receipts).containsExactly(secondReceiptReduced)
        assertThat(tags).containsExactly(sampleTag)
    }

    @Test
    fun `Update receipt name`(){
        receiptRepository.insertReceipt(sampleReceipt)
        val updatedReceipt = sampleReceipt.copy(
            name = "Aldi"
        )

        receiptRepository.updateReceipt(updatedReceipt)

        val receipt = receiptRepository.selectReceiptById(sampleReceipt.id)
        assertThat(receipt).isEqualTo(updatedReceipt)
    }

    @Test
    fun `Update receipt tags, replace tag`() = runBlocking {
        val newTag = TagEntity(1, "Aldi")
        val updatedReceipt = sampleReceipt.copy(
            tags = listOf(newTag)
        )
        receiptRepository.insertReceipt(sampleReceipt)

        receiptRepository.updateReceipt(updatedReceipt)

        val receipt = receiptRepository.selectReceiptById(sampleReceipt.id)
        val tags = tagRepository.selectAll().first()
        assertThat(receipt).isEqualTo(updatedReceipt)
        assertThat(tags).containsExactly(newTag)
    }

    @Test
    fun `Update receipt tags, add a new tag`() = runBlocking {
        val newTag = TagEntity(2, "Aldi")
        val updatedReceipt = sampleReceipt.copy(
            tags = listOf(sampleTag, newTag)
        )
        receiptRepository.insertReceipt(sampleReceipt)

        receiptRepository.updateReceipt(updatedReceipt)

        val receipt = receiptRepository.selectReceiptById(sampleReceipt.id)
        val tags = tagRepository.selectAll().first()
        assertThat(receipt).isEqualTo(updatedReceipt)
        assertThat(tags).containsExactly(sampleTag, newTag)
    }

    @Test
    fun `Update receipt tags, remove a tag`() = runBlocking {
        val newTag = TagEntity(2, "Aldi")
        val updatedReceipt = sampleReceipt.copy(
            tags = listOf(sampleTag, newTag)
        )
        receiptRepository.insertReceipt(updatedReceipt)

        receiptRepository.updateReceipt(sampleReceipt)

        val receipt = receiptRepository.selectReceiptById(sampleReceipt.id)
        val tags = tagRepository.selectAll().first()
        assertThat(receipt).isEqualTo(sampleReceipt)
        assertThat(tags).containsExactly(sampleTag)
    }

    @Test
    fun `Update receipt items, replace item`(){
        val newItem = sampleItems[1].copy(
            id = 4,
            itemId = 3,
            name = "Alma",
            date = Instant.fromEpochSeconds(4)
        )
        val updatedReceipt = sampleReceipt.copy(
            items = listOf(newItem, sampleItems[0], sampleItems[2])
        )
        receiptRepository.insertReceipt(sampleReceipt)

        receiptRepository.updateReceipt(updatedReceipt)

        val receipt = receiptRepository.selectReceiptById(sampleReceipt.id)
        val items = itemRepository.selectAll()
        assertThat(receipt).isEqualTo(updatedReceipt)
        assertThat(items).containsExactly(newItem, sampleItems[0], sampleItems[2])
    }

    @Test
    fun `Update receipt items, add a new item`(){
        val newItem = sampleItems[1].copy(
            id = 4,
            itemId = 3,
            name = "Alma",
            date = Instant.fromEpochSeconds(4)
        )
        val updatedReceipt = sampleReceipt.copy(
            items = listOf(newItem, sampleItems[0], sampleItems[1], sampleItems[2])
        )
        receiptRepository.insertReceipt(sampleReceipt)

        receiptRepository.updateReceipt(updatedReceipt)

        val receipt = receiptRepository.selectReceiptById(sampleReceipt.id)
        val items = itemRepository.selectAll()
        assertThat(receipt).isEqualTo(updatedReceipt)
        assertThat(items).containsExactly(newItem, sampleItems[0], sampleItems[1], sampleItems[2])
    }

    @Test
    fun `Update receipt items, remove a item`(){
        val newItem = sampleItems[1].copy(
            id = 4,
            itemId = 3,
            name = "Alma",
            date = Instant.fromEpochSeconds(4)
        )
        val updatedReceipt = sampleReceipt.copy(
            items = listOf(sampleItems[0], sampleItems[1], sampleItems[2], newItem)
        )
        receiptRepository.insertReceipt(updatedReceipt)

        receiptRepository.updateReceipt(sampleReceipt)

        val receipt = receiptRepository.selectReceiptById(sampleReceipt.id)
        val items = itemRepository.selectAll()
        assertThat(receipt).isEqualTo(sampleReceipt)
        assertThat(items).containsExactly(sampleItems[0], sampleItems[1], sampleItems[2])
    }

    @Test
    fun `Update receipt items, update item category`(){
        val newCategory = ItemCategoryEntity(
            id = 3,
            name = "Aldi",
            color = null
        )
        val newItem = sampleItems[1].copy(
            category = newCategory
        )
        val updatedReceipt = sampleReceipt.copy(
            items = listOf(sampleItems[0], newItem, sampleItems[2])
        )
        receiptRepository.insertReceipt(sampleReceipt)
        categoryRepository.insertCategory(newCategory)

        receiptRepository.updateReceipt(updatedReceipt)

        val receipt = receiptRepository.selectReceiptById(sampleReceipt.id)
        val items = itemRepository.selectAll()
        val categories = categoryRepository.selectAllCategory()
        assertThat(receipt).isEqualTo(updatedReceipt)
        assertThat(items).containsExactly(sampleItems[0], newItem, sampleItems[2])
        assertThat(categories).containsExactly(defaultCategory, sampleCategory, newCategory)
    }
}