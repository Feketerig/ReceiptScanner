package hu.levente.fazekas.receiptscanner.database

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import hu.levente.fazekas.Item
import hu.levente.fazekas.Receipt
import hu.levente.fazekas.database.ReceiptDatabase
import hu.levente.fazekas.receiptscanner.database.fake.defaultCategory
import hu.levente.fazekas.receiptscanner.database.fake.sampleCategory
import hu.levente.fazekas.receiptscanner.database.DateAdapter
import hu.levente.fazekas.receiptscanner.database.ItemCategoryEntity
import hu.levente.fazekas.receiptscanner.database.SqlDelightItemCategoryRepository
import hu.levente.fazekas.receiptscanner.database.SqlDelightTagRepository
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import java.util.Properties

class SqlDelightItemCategoryRepositoryTest {

    private lateinit var db: ReceiptDatabase
    private lateinit var itemCategoryRepository: SqlDelightItemCategoryRepository

    @Before
    fun setUp() {
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
        itemCategoryRepository = SqlDelightItemCategoryRepository(db)
        //Inserting a default category to have a fallback category when deleting
        itemCategoryRepository.insertCategory(defaultCategory)
    }

    @Test
    fun `Insert category successfully`(){
        itemCategoryRepository.insertCategory(sampleCategory)

        val categories = itemCategoryRepository.selectAllCategory()
        assertEquals(2, categories.size)
        assertEquals(defaultCategory, categories[0])
        assertEquals(sampleCategory, categories[1])
    }

    @Test
    fun `Insert 2 categories with the same name, throws exception`(){
        itemCategoryRepository.insertCategory(sampleCategory)

        val exception = assertThrows(Exception::class.java) {
            itemCategoryRepository.insertCategory(sampleCategory)
        }

        assertEquals("[SQLITE_CONSTRAINT_UNIQUE] A UNIQUE constraint failed (UNIQUE constraint failed: ItemCategory.name)", exception.message)
    }

    @Test
    fun `Select all categories returns all the categories in good format`(){
        itemCategoryRepository.insertCategory(sampleCategory)

        val categories = db.itemCategoryQueries.selectAll(
            mapper = { id, name, color ->
                ItemCategoryEntity(
                    id = id,
                    name = name,
                    color = color
                )
            }
        ).executeAsList()

        assertEquals(2, categories.size)
        assertEquals(defaultCategory, categories[0])
        assertEquals(sampleCategory, categories[1])
    }

    @Test
    fun `Update category name successfully`(){
        itemCategoryRepository.insertCategory(sampleCategory)
        val newCategory = ItemCategoryEntity(2, "Gyümölcs", 789)

        itemCategoryRepository.updateCategory(newCategory)

        val updatedCategories = itemCategoryRepository.selectAllCategory()
        assertEquals(2, updatedCategories.size)
        assertEquals(defaultCategory, updatedCategories[0])
        assertEquals(newCategory, updatedCategories[1])
    }

    @Test
    fun `Update category color successfully`(){
        itemCategoryRepository.insertCategory(sampleCategory)
        val newCategory = ItemCategoryEntity(2, "Tejtermék", 123)

        itemCategoryRepository.updateCategory(newCategory)

        val updatedCategories = itemCategoryRepository.selectAllCategory()
        assertEquals(2, updatedCategories.size)
        assertEquals(defaultCategory, updatedCategories[0])
        assertEquals(newCategory, updatedCategories[1])
    }

    @Test
    fun `Delete category, only default left`(){
        itemCategoryRepository.insertCategory(sampleCategory)

        itemCategoryRepository.deleteCategory(sampleCategory.id)

        val deletedCategories = itemCategoryRepository.selectAllCategory()
        assertEquals(1, deletedCategories.size)
        assertEquals(defaultCategory, deletedCategories[0])
    }
}