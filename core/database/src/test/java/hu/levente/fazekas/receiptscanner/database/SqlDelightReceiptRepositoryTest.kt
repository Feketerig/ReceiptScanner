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
import kotlinx.datetime.Instant
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Properties

class SqlDelightReceiptRepositoryTest {

    private lateinit var db: ReceiptDatabase
    private lateinit var itemRepository: SqlDelightItemRepository
    private lateinit var categoryRepository: SqlDelightItemCategoryRepository
    private lateinit var receiptRepository: SqlDelightReceiptRepository
    private lateinit var tagRepository: SqlDelightTagRepository

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
        itemRepository = SqlDelightItemRepository(db)
        categoryRepository = SqlDelightItemCategoryRepository(db)
        //Inserting a default category to have a fallback category when deleting
        categoryRepository.insertCategory(defaultCategory)
        categoryRepository.insertCategory(sampleCategory)
        receiptRepository = SqlDelightReceiptRepository(db)
        tagRepository = SqlDelightTagRepository(db)
    }

    @Test
    fun `Insert Receipt with no tags successfully`(){
        val receiptWithNoTags = sampleReceipt.copy(tags = emptyList())
        receiptRepository.insertReceipt(receiptWithNoTags)

        val receipt = receiptRepository.selectReceiptById(sampleReceipt.id)
        val items = itemRepository.selectAll()
        val tags = tagRepository.selectByReceiptId(receipt.id)
        assertThat(receipt).isEqualTo(receiptWithNoTags)
        assertThat(items).containsExactly(*sampleItems.toTypedArray())
        assertThat(tags).isEmpty()
    }

    @Test
    fun `Insert Receipt with tags successfully`(){
        receiptRepository.insertReceipt(sampleReceipt)

        val receipt = receiptRepository.selectReceiptById(sampleReceipt.id)
        val items = itemRepository.selectAll()
        val tags = tagRepository.selectByReceiptId(receipt.id)
        assertThat(receipt).isEqualTo(sampleReceipt)
        assertThat(items).containsExactly(*sampleItems.toTypedArray())
        assertThat(tags).containsExactly(sampleTag)
    }

    @Test
    fun `Select all receipt without items`(){
        val secondReceipt = sampleReceipt.copy(
            id = 2,
            name = "Aldi",
            date = Instant.fromEpochSeconds(2),
            tags = emptyList(),
        )
        receiptRepository.insertReceipt(sampleReceipt)
        receiptRepository.insertReceipt(secondReceipt)

        val receipts = receiptRepository.selectAllReducedReceipt()
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
}