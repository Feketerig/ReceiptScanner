package hu.levente.fazekas.receiptscanner.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import hu.levente.fazekas.database.ReceiptDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class SqlDelightTagDataSource(
    private val db: ReceiptDatabase
) {
    fun selectAllTag(): Flow<List<TagEntity>> {
        return db.tagQueries.selectAll { id, name -> TagEntity(id, name) }.asFlow().mapToList(Dispatchers.IO)
    }

    fun insertTag(tagName: String){
        db.tagQueries.insert(tagName)
    }

    fun deleteTag(tagId: Long){
        db.tagQueries.deleteById(tagId)
    }

    fun updateTag(tag: TagEntity){
        db.tagQueries.update(id = tag.id,name = tag.name)
    }

    fun selectByReceiptId(receiptId: Long): List<TagEntity> {
        return db.receiptTagCrossRefQueries.selectByReceiptId(receiptId,
            mapper = { id, name -> TagEntity(id, name) }
        ).executeAsList()
    }
}