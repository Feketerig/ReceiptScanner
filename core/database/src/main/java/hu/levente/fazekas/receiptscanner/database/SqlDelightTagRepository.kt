package hu.levente.fazekas.receiptscanner.database

import hu.levente.fazekas.database.ReceiptDatabase

class SqlDelightTagRepository(
    private val db: ReceiptDatabase
) {
    fun selectAllTag(): List<TagEntity>{
        return db.tagQueries.selectAll { id, name -> TagEntity(id, name) }.executeAsList()
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
}