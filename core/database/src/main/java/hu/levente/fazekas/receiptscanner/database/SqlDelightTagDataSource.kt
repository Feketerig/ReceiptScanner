package hu.levente.fazekas.receiptscanner.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import hu.levente.fazekas.database.ReceiptDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class SqlDelightTagDataSource(
    private val db: ReceiptDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): TagDataSource {
    override fun selectAll(): Flow<List<TagEntity>> {
        return db.tagQueries.selectAll { id, name -> TagEntity(id, name) }
            .asFlow().mapToList(dispatcher)
    }

    override fun selectByReceiptId(receiptId: Long): Flow<List<TagEntity>> {
        return db.receiptTagCrossRefQueries.selectByReceiptId(receiptId,
            mapper = { id, name -> TagEntity(id, name) }
        ).asFlow().mapToList(dispatcher)
    }

    override fun selectByName(tagName: String): Flow<TagEntity> {
        return db.tagQueries.selectByName(tagName,
            mapper = { id, name -> TagEntity(id, name) }
        ).asFlow().mapToOne(dispatcher)
    }

    override fun selectWithFilter(query: String): Flow<List<TagEntity>> {
        return db.tagQueries.selectWithFilter(query,
            mapper = { id, name -> TagEntity(id, name) }
        ).asFlow().mapToList(dispatcher)
    }

    override suspend fun insert(tagName: String){
        db.tagQueries.insert(tagName)
    }

    override suspend fun delete(tagId: Long){
        db.tagQueries.deleteById(tagId)
    }

    override suspend fun update(tag: TagEntity){
        db.tagQueries.update(id = tag.id,name = tag.name)
    }
}