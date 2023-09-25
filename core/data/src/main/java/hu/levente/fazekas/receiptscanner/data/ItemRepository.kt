package hu.levente.fazekas.receiptscanner.data

import hu.levente.fazekas.receiptscanner.database.ItemEntity

interface ItemRepository {
    fun selectAll(): List<ItemEntity>
    fun selectItemById(id: Long): ItemEntity
    fun selectAllByItemId(id: Long): List<ItemEntity>
    fun selectAllByCategory(categoryId: Long): List<ItemEntity>
    fun selectAllByReceiptId(receiptId: Long): List<ItemEntity>
    fun insertItem(itemEntity: ItemEntity)
    fun updateItem(newItemEntity: ItemEntity)
    fun deleteItem(id: Long)
}