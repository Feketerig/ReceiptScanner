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
import hu.levente.fazekas.receiptscanner.database.fake.sampleTag
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Properties

class SqlDelightTagDataSourceTest {

    private lateinit var db: ReceiptDatabase
    private lateinit var tagDataSource: TagDataSource
    private val scope = TestScope()

    @BeforeEach
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
        tagDataSource = SqlDelightTagDataSource(db, StandardTestDispatcher(scope.testScheduler))
    }

    @Test
    fun `Insert tag successfully`() = scope.runTest {
        tagDataSource.insert(sampleTag.name)

        val tags = tagDataSource.selectAll().first()
        assertThat(tags).containsExactly(sampleTag)
    }


    @Test
    fun `Insert 2 tags with the same name, second ignored`() = scope.runTest {
        tagDataSource.insert(sampleTag.name)
        tagDataSource.insert(sampleTag.name)

        val tags = tagDataSource.selectAll().first()
        assertThat(tags).containsExactly(sampleTag)
    }

    @Test
    fun `Select all tags returns all the tags in good format`() = scope.runTest {
        tagDataSource.insert(sampleTag.name)

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
    fun `Update tag name successfully`() = scope.runTest {
        tagDataSource.insert(sampleTag.name)
        val newTag = TagEntity(1, "Aldi")

        tagDataSource.update(newTag)


        val tags = tagDataSource.selectAll().first()
        assertThat(tags).containsExactly(newTag)
    }

    @Test
    fun `Delete tag, tagRepository becomes empty`() = scope.runTest {
        tagDataSource.insert(sampleTag.name)

        tagDataSource.delete(sampleTag.id)

        val tags = tagDataSource.selectAll().first()
        assertThat(tags).isEmpty()
    }

    @Test
    fun `Select tags by a receiptId`() = scope.runTest {
        tagDataSource.insert(sampleTag.name)
        tagDataSource.insert("NewTag")
        val id = db.receiptQueries.insert(
            name = "Test",
            date = Instant.fromEpochSeconds(1),
            currency = Currency.HUF,
            sumOfPrice = 1253,
            description = null,
            imageUri = ""
        ).executeAsOne()

        db.receiptTagCrossRefQueries.insert(id, sampleTag.id)

        val allTags = tagDataSource.selectAll().first()
        val tagsByReceiptId = tagDataSource.selectByReceiptId(1).first()
        assertThat(allTags).containsExactly(sampleTag, TagEntity(2,"NewTag"))
        assertThat(tagsByReceiptId).containsExactly(sampleTag)
    }

    @Test
    fun `Select tag by name`() = scope.runTest {
        tagDataSource.insert(sampleTag.name)

        val tags = tagDataSource.selectByName("Auchan").first()
        assertThat(tags).isEqualTo(sampleTag)
    }
}