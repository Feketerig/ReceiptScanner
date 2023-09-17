package hu.levente.fazekas.receiptscanner.database

import hu.levente.fazekas.database.ReceiptDatabase

class SqlDelightItemRepository(
    val db: ReceiptDatabase
) {
    fun selectAllItem(): List<ItemEntity>{
        return db.itemQueries.selectAll { id, itemId, name, count, price, unit, categoryId, categoryName, categoryColor, date, currency, receiptId ->
            ItemEntity(
                id = id,
                itemId = itemId,
                name = name,
                quantity = count,
                price = price,
                unit = unit,
                category = ItemCategoryEntity(
                        id = categoryId,
                        name = categoryName,
                        color = categoryColor
                ),
                date = date,
                currency = currency,
                receiptId = receiptId
            )
        }.executeAsList()
    }

    fun insertItem(itemEntity: ItemEntity){
        db.itemQueries.insert(
            itemName = itemEntity.name,
            categoryId = itemEntity.category.id,
            count = itemEntity.quantity,
            price = itemEntity.price,
            unit = itemEntity.unit,
            date = itemEntity.date,
            currency = itemEntity.currency,
            receiptId = itemEntity.receiptId
        )
    }

    fun updateItem(newItemEntity: ItemEntity){
        db.transaction {
            val oldItem = db.itemQueries.selectById(newItemEntity.id).executeAsOne()
            if (oldItem.itemId != newItemEntity.itemId) {
                db.itemIdQueries.insert(newItemEntity.name)
            } else if (oldItem.name != newItemEntity.name) {
                db.itemIdQueries.update(newItemEntity.itemId, newItemEntity.name)
            }
            if (oldItem.categoryId != newItemEntity.category.id) {
                db.itemCategoryQueries.deleteById(oldItem.categoryId)
                db.itemCategoryQueries.insert(
                    newItemEntity.category.name,
                    newItemEntity.category.color
                )
            } else if (oldItem.categoryName != newItemEntity.category.name || oldItem.categoryColor != newItemEntity.category.color) {
                db.itemCategoryQueries.update(
                    newItemEntity.category.id,
                    newItemEntity.category.name,
                    newItemEntity.category.color
                )
            }
            db.itemQueries.update(
                itemName = newItemEntity.name,
                categoryName = newItemEntity.category.name,
                id = newItemEntity.id,
                count = newItemEntity.quantity,
                price = newItemEntity.price,
                unit = newItemEntity.unit,
                date = newItemEntity.date,
                currency = newItemEntity.currency,
                receiptId = newItemEntity.receiptId
            )
            if (oldItem.itemId != newItemEntity.itemId) {
                db.itemIdQueries.deleteById(oldItem.itemId)
            }
        }
    }

    fun deleteItem(id: Long){
        db.transaction{
            val itemId = db.itemQueries.selectById(id).executeAsOne().itemId
            db.itemQueries.deleteById(id)
            val itemsWithSameItemId = db.itemQueries.selectAllByItemId(itemId).executeAsList()
            if (itemsWithSameItemId.isEmpty()){
                db.itemIdQueries.deleteById(itemId)
            }
        }
    }
}