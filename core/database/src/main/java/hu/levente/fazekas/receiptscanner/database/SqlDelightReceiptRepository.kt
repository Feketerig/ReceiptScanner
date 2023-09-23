package hu.levente.fazekas.receiptscanner.database

import hu.levente.fazekas.database.ReceiptDatabase

class SqlDelightReceiptRepository(
    val db: ReceiptDatabase
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
                    tagId = tag.id
                )
            }
            receipt.items.forEach{item ->
                db.itemQueries.insert(
                    itemName = item.name,
                    categoryId = item.category.id,
                    quantity = item.quantity,
                    price = item.price,
                    unit = item.unit,
                    date = item.date,
                    currency = item.currency,
                    receiptId = receipt.id,
                )
            }
        }
    }

    fun deleteReceipt(receiptId: Long){
        db.receiptQueries.deleteById(receiptId)
    }

    fun updateReceipt(receipt: ReceiptEntity){

    }

    fun selectReceiptById(receiptId: Long): ReceiptEntity{
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