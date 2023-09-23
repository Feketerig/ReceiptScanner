package hu.levente.fazekas.receiptscanner.database

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.hasMessage
import assertk.assertions.isEmpty
import hu.levente.fazekas.Item
import hu.levente.fazekas.Receipt
import hu.levente.fazekas.database.ReceiptDatabase
import hu.levente.fazekas.receiptscanner.database.fake.sampleTag
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Properties


class SqlDelightTagRepositoryTest {

    private lateinit var db: ReceiptDatabase
    private lateinit var tagRepository: SqlDelightTagRepository

    @BeforeEach
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
        assertThat(tags).containsExactly(sampleTag)
    }

    @Test
    fun `Insert 2 tags with the same name, throws exception`(){
        tagRepository.insertTag(sampleTag.name)

        assertFailure {
            tagRepository.insertTag(sampleTag.name)
        }.hasMessage("[SQLITE_CONSTRAINT_UNIQUE] A UNIQUE constraint failed (UNIQUE constraint failed: Tag.name)")
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

        assertThat(tags).containsExactly(sampleTag)
    }

    @Test
    fun `Update tag name successfully`(){
        tagRepository.insertTag(sampleTag.name)
        val newTag = TagEntity(1, "Aldi")

        tagRepository.updateTag(newTag)


        val tags = tagRepository.selectAllTag()
        assertThat(tags).containsExactly(newTag)
    }

    @Test
    fun `Delete tag, tagRepository becomes empty`(){
        tagRepository.insertTag(sampleTag.name)

        tagRepository.deleteTag(sampleTag.id)

        val tags = tagRepository.selectAllTag()
        assertThat(tags).isEmpty()
    }
}