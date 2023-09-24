package hu.levente.fazekas.receiptscanner.database

import hu.levente.fazekas.database.ReceiptDatabase

class SqlDelightReceiptDataSource(
    val db: ReceiptDatabase,
    val itemRepository: SqlDelightItemDataSource
) {
    fun insertReceipt(receipt: ReceiptEntity){
        db.transaction {
            db.receiptQueries.insert(
                name = receipt.name,
                date = receipt.date,
                currency = receipt.currency,
                sumOfPrice = receipt.sumOfPrice,
                description = receipt.description,
                imageUri = receipt.imageUri
            )
            receipt.tags.forEach { tag ->
                db.tagQueries.insert(tag.name)
                db.receiptTagCrossRefQueries.insert(
                    receiptId = receipt.id,
                    tagId = db.tagQueries.selectByName(tag.name).executeAsOne().id
                )
            }
            receipt.items.forEach{item ->
                itemRepository.insertItem(item)
            }
        }
    }

    fun deleteReceipt(receiptId: Long){
        db.transaction {
            val tagIds = db.receiptTagCrossRefQueries.selectByReceiptId(receiptId, mapper = {id, _ -> id }).executeAsList()
            db.receiptQueries.deleteById(receiptId)
            tagIds.forEach { id ->
                val receipts = db.receiptTagCrossRefQueries.selectByTagId(id).executeAsList()
                if (receipts.isEmpty()) {
                    db.tagQueries.deleteById(id)
                }
            }
        }
    }

    fun updateReceipt(receipt: ReceiptEntity) {
        db.transaction {
            val oldReceipt = selectReceiptById(receipt.id)
            if (oldReceipt.items != receipt.items){
                val removeItems = oldReceipt.items - receipt.items.toSet()
                val plusItems = receipt.items - oldReceipt.items.toSet()
                val updatedRemoveItems = removeItems.toMutableList()
                val updatedPlusItems = plusItems.toMutableList()

                plusItems.forEach { plusItem ->
                    removeItems.forEach {removeItem ->
                        if (removeItem.id == plusItem.id){
                            itemRepository.updateItem(plusItem)
                            updatedRemoveItems.remove(removeItem)
                            updatedPlusItems.remove(plusItem)
                        }
                    }
                }

                updatedRemoveItems.forEach {item ->
                    itemRepository.deleteItem(item.id)
                }
                updatedPlusItems.forEach {item ->
                    db.itemQueries.insert(
                        itemName = item.name,
                        categoryId = item.category.id,
                        quantity = item.quantity,
                        price = item.price,
                        unit = item.unit,
                        date = item.date,
                        currency = item.currency,
                        receiptId = item.receiptId
                    )
                }
            }
            if (oldReceipt.tags != receipt.tags){
                val removeTags = oldReceipt.tags - receipt.tags
                val plusTags = receipt.tags - oldReceipt.tags

                removeTags.forEach { tag ->
                    val tagId = db.tagQueries.selectByName(tag.name).executeAsOne().id
                    db.receiptTagCrossRefQueries.deleteById(receiptId = receipt.id, tagId = tagId)
                    val receipts = db.receiptTagCrossRefQueries.selectByTagId(tagId).executeAsList()
                    if (receipts.isEmpty()) {
                        db.tagQueries.deleteById(tagId)
                    }
                }
                plusTags.forEach {tag ->
                    db.tagQueries.insert(tag.name)
                    db.receiptTagCrossRefQueries.insert(
                        receiptId = receipt.id,
                        tagId = db.tagQueries.selectByName(tag.name).executeAsOne().id
                    )
                }
            }

            db.receiptQueries.update(
                id = receipt.id,
                name = receipt.name,
                date = receipt.date,
                currency = receipt.currency,
                sumOfPrice = receipt.sumOfPrice,
                description = receipt.description,
                imageUri = receipt.imageUri
            )
        }
    }

    fun selectReceiptById(receiptId: Long): ReceiptEntity {
        return db.transactionWithResult {
            val receipt = db.receiptQueries.selectById(receiptId).executeAsOne()
            val items = db.itemQueries.selectByReceiptId(receiptId, mapper = { id, itemId, name, count, price, unit, categoryId,categoryName,categoryColor, date, currency, receiptId ->
                ItemEntity(id, itemId,name,count,price,unit, ItemCategoryEntity(categoryId, categoryName,categoryColor),date,currency, receiptId)
            }).executeAsList()
            val tags = db.receiptTagCrossRefQueries.selectByReceiptId(receiptId, mapper = { id, name -> TagEntity(id, name) }).executeAsList()

            ReceiptEntity(
                id = receipt.id,
                name = receipt.name,
                date = receipt.date,
                currency = receipt.currency,
                sumOfPrice = receipt.sumOfPrice,
                description = receipt.description ?: "",
                imageUri = receipt.imageUri,
                tags = tags,
                items = items
            )
        }
    }

    fun selectAllReducedReceipt(): List<ReducedReceiptEntity>{
        return db.transactionWithResult {
            db.receiptQueries.selectAllReduced { id, name, date, currency, sumOfPrice ->
                val tags = db.receiptTagCrossRefQueries.selectByReceiptId(id,
                    mapper = {id, name -> TagEntity(id,name)}
                ).executeAsList()
                ReducedReceiptEntity(
                    id = id,
                    name = name,
                    date = date,
                    currency = currency,
                    sumOfPrice = sumOfPrice,
                    tags = tags
                )
            }.executeAsList()
        }
    }
}