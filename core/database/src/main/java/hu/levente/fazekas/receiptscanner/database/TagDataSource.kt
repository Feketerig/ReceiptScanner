package hu.levente.fazekas.receiptscanner.database

import kotlinx.coroutines.flow.Flow

interface TagDataSource {

    fun selectAll(): Flow<List<TagEntity>>

    fun selectByReceiptId(receiptId: Long): Flow<List<TagEntity>>

    fun selectByName(tagName: String): Flow<TagEntity>

    fun selectWithFilter(query: String): Flow<List<TagEntity>>

    suspend fun insert(tagName: String)

    suspend fun delete(tagId: Long)

    suspend fun update(tag: TagEntity)
}