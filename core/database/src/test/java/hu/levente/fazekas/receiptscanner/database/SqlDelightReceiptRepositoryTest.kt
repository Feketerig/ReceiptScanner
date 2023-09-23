package hu.levente.fazekas.receiptscanner.database

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import hu.levente.fazekas.Item
import hu.levente.fazekas.Receipt
import hu.levente.fazekas.database.ReceiptDatabase
import hu.levente.fazekas.receiptscanner.database.fake.defaultCategory
import hu.levente.fazekas.receiptscanner.database.fake.sampleCategory
import hu.levente.fazekas.receiptscanner.database.fake.sampleItems
import hu.levente.fazekas.receiptscanner.database.fake.sampleReceipt
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Properties

class SqlDelightReceiptRepositoryTest {

    private lateinit var db: ReceiptDatabase
    private lateinit var itemRepository: SqlDelightItemRepository
    private lateinit var categoryRepository: SqlDelightItemCategoryRepository
    private lateinit var receiptRepository: SqlDelightReceiptRepository

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
    }

    @Test
    fun `Insert Receipt with category successfully`(){
        receiptRepository.insertReceipt(sampleReceipt)

        val receipt = receiptRepository.selectReceiptById(sampleReceipt.id)
        val items = itemRepository.selectAll()
        assertThat(receipt).isEqualTo(sampleReceipt)
        assertThat(items).containsExactly(sampleItems[0], sampleItems[1], sampleItems[2])
    }
}