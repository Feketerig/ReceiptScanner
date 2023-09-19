package hu.levente.fazekas.receiptscanner.database

import hu.levente.fazekas.database.ReceiptDatabase

class SqlDelightItemCategoryRepository(
    private val db: ReceiptDatabase
) {
    fun selectAllCategory(): List<ItemCategoryEntity>{
        return db.itemCategoryQueries.selectAll { id, name, color ->
                ItemCategoryEntity(
                    id = id,
                    name = name,
                    color = color
                )
            }.executeAsList()
    }

    fun insertCategory(category: ItemCategoryEntity){
        db.itemCategoryQueries.insert(
            name = category.name,
            color = category.color
        )
    }

    fun updateCategory(category: ItemCategoryEntity){
        db.itemCategoryQueries.update(id = category.id, name = category.name, color = category.color)
    }

    fun deleteCategory(categoryId: Long){
        db.itemCategoryQueries.deleteById(categoryId)
    }
}