package hu.levente.fazekas.database

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import hu.levente.fazekas.Item
import hu.levente.fazekas.Receipt
import hu.levente.fazekas.database.fake.sampleCategory
import hu.levente.fazekas.database.fake.sampleTag
import hu.levente.fazekas.receiptscanner.database.DateAdapter
import hu.levente.fazekas.receiptscanner.database.ItemCategoryEntity
import hu.levente.fazekas.receiptscanner.database.SqlDelightItemCategoryRepository
import hu.levente.fazekas.receiptscanner.database.SqlDelightItemRepository
import hu.levente.fazekas.receiptscanner.database.SqlDelightTagRepository
import hu.levente.fazekas.receiptscanner.database.TagEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
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
                dateAdapter = DateAdapter(),
                currencyAdapter = EnumColumnAdapter()
            )
        )
    }

    @Test
    fun tagRepositoryTest(){
        val tagRepository = SqlDelightTagRepository(db)

        //insert
        tagRepository.insertTag(sampleTag.name)

        val tags = tagRepository.selectAllTag()

        assertEquals(1, tags.size)
        assertEquals(sampleTag, tags[0])

        //update
        val newTag = TagEntity(1, "Aldi")
        tagRepository.updateTag(newTag)

        val updatedTags = tagRepository.selectAllTag()

        assertEquals(1, updatedTags.size)
        assertEquals(newTag, updatedTags[0])

        //delete
        tagRepository.deleteTag(newTag.id)

        val deletedTags = tagRepository.selectAllTag()

        assertEquals(0, deletedTags.size)

        //insert same name twice throws exception
        tagRepository.insertTag(sampleTag.name)

        val exception = assertThrows(Exception::class.java) {
            tagRepository.insertTag(sampleTag.name)
        }

        assertEquals("[SQLITE_CONSTRAINT_UNIQUE] A UNIQUE constraint failed (UNIQUE constraint failed: Tag.name)", exception.message)
    }



}