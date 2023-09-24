package hu.levente.fazekas.receiptscanner.database

import hu.levente.fazekas.database.ReceiptDatabase

class SqlDelightItemRepository(
    val db: ReceiptDatabase
) {
    fun selectAll(): List<ItemEntity>{
        return db.itemQueries.selectAll { id, itemId, name, quantity, price, unit, categoryId, categoryName, categoryColor, date, currency, receiptId ->
            ItemEntity(
                id = id,
                itemId = itemId,
                name = name,
                quantity = quantity,
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

    fun selectItemById(id: Long): ItemEntity{
        return db.itemQueries.selectById(id) { id, itemId, name, quantity, price, unit, categoryId, categoryName, categoryColor, date, currency, receiptId ->
            ItemEntity(
                id = id,
                itemId = itemId,
                name = name,
                quantity = quantity,
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
        }.executeAsOne()
    }

    fun selectAllByItemId(id: Long): List<ItemEntity>{
        return db.itemQueries.selectAllByItemId(id) { id, itemId, name, quantity, price, unit, categoryId, categoryName, categoryColor, date, currency, receiptId ->
            ItemEntity(
                id = id,
                itemId = itemId,
                name = name,
                quantity = quantity,
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

    fun selectAllByCategory(categoryId: Long): List<ItemEntity>{
        return db.itemQueries.selectAllByCategory(categoryId) { id, itemId, name, quantity, price, unit, categoryId, categoryName, categoryColor, date, currency, receiptId ->
            ItemEntity(
                id = id,
                itemId = itemId,
                name = name,
                quantity = quantity,
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

    fun selectAllByReceiptId(receiptId: Long): List<ItemEntity>{
        return db.itemQueries.selectByReceiptId(receiptId) { id, itemId, name, quantity, price, unit, categoryId, categoryName, categoryColor, date, currency, receiptId ->
            ItemEntity(
                id = id,
                itemId = itemId,
                name = name,
                quantity = quantity,
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
            quantity = itemEntity.quantity,
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
                db.itemIdQueries.update(itemId = newItemEntity.itemId,name = newItemEntity.name)
            }
             if (oldItem.categoryName != newItemEntity.category.name || oldItem.categoryColor != newItemEntity.category.color) {
                db.itemCategoryQueries.update(
                    id = newItemEntity.category.id,
                    name = newItemEntity.category.name,
                    color = newItemEntity.category.color
                )
            }
            db.itemQueries.update(
                itemName = newItemEntity.name,
                categoryName = newItemEntity.category.name,
                id = newItemEntity.id,
                quantity = newItemEntity.quantity,
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