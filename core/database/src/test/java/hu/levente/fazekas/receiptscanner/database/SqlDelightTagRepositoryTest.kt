package hu.levente.fazekas.receiptscanner.database

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import hu.levente.fazekas.Item
import hu.levente.fazekas.Receipt
import hu.levente.fazekas.database.ReceiptDatabase
import hu.levente.fazekas.receiptscanner.database.fake.sampleTag
import hu.levente.fazekas.receiptscanner.database.DateAdapter
import hu.levente.fazekas.receiptscanner.database.SqlDelightTagRepository
import hu.levente.fazekas.receiptscanner.database.TagEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.util.Properties


class SqlDelightTagRepositoryTest {

    private lateinit var db: ReceiptDatabase
    private lateinit var tagRepository: SqlDelightTagRepository

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
        tagRepository = SqlDelightTagRepository(db)
    }

    @Test
    fun `Insert tag successfully`(){
        tagRepository.insertTag(sampleTag.name)

        val tags = tagRepository.selectAllTag()
        assertEquals(1, tags.size)
        assertEquals(sampleTag, tags[0])
    }

    @Test
    fun `Insert 2 tags with the same name, throws exception`(){
        tagRepository.insertTag(sampleTag.name)

        val exception = assertThrows(Exception::class.java) {
            tagRepository.insertTag(sampleTag.name)
        }

        assertEquals("[SQLITE_CONSTRAINT_UNIQUE] A UNIQUE constraint failed (UNIQUE constraint failed: Tag.name)", exception.message)
    }

    @Test
    fun `Select all tags returns all the tags in good format`(){
        tagRepository.insertTag(sampleTag.name)

        val tags = db.tagQueries.selectAll(
            mapper = { id, name ->
                TagEntity(
                    id = id,
                    name = name,
                )
            }
        ).executeAsList()

        assertEquals(1, tags.size)
        assertEquals(sampleTag, tags[0])
    }

    @Test
    fun `Update tag name successfully`(){
        tagRepository.insertTag(sampleTag.name)
        val newTag = TagEntity(1, "Aldi")

        tagRepository.updateTag(newTag)


        val updatedTags = tagRepository.selectAllTag()
        assertEquals(1, updatedTags.size)
        assertEquals(newTag, updatedTags[0])
    }

    @Test
    fun `Delete tag, tagRepository becomes empty`(){
        tagRepository.insertTag(sampleTag.name)

        tagRepository.deleteTag(sampleTag.id)

        val deletedTags = tagRepository.selectAllTag()
        assertEquals(0, deletedTags.size)
    }
}