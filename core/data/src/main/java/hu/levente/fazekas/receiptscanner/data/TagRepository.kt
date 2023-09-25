package hu.levente.fazekas.receiptscanner.data

import hu.levente.fazekas.receiptscanner.database.TagEntity

interface TagRepository {
    fun selectAllTag(): List<TagEntity>
    fun insertTag(tagName: String)
    fun deleteTag(tagId: Long)
    fun updateTag(tag: TagEntity)
    fun selectByReceiptId(receiptId: Long): List<TagEntity>
}