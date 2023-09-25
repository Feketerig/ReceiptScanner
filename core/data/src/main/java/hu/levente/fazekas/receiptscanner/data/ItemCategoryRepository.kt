package hu.levente.fazekas.receiptscanner.data

import hu.levente.fazekas.receiptscanner.database.ItemCategoryEntity

interface ItemCategoryRepository {
    fun selectAllCategory(): List<ItemCategoryEntity>
    fun insertCategory(category: ItemCategoryEntity)
    fun updateCategory(category: ItemCategoryEntity)
    fun deleteCategory(categoryId: Long)
}