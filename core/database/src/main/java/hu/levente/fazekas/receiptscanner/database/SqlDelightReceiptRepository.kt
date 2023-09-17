package hu.levente.fazekas.receiptscanner.database

import hu.levente.fazekas.database.ReceiptDatabase

class SqlDelightReceiptRepository(
    val db: ReceiptDatabase
) {
    fun insertReceipt(receipt: ReceiptEntity){
        db.transaction {
            db.receiptQueries.insert(
                id = receipt.id,
                name = receipt.name,
                date = receipt.date,
                currency = receipt.currency,
                sumOfPrice = receipt.sumOfPrice,
                description = receipt.description,
                imageUri = receipt.imageUri
            )
            receipt.items.forEach{item ->
                db.itemQueries.insert(
                    itemName = item.name,
                    categoryId = item.category.id,
                    count = item.quantity,
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
            val items = db.itemQueries.selectByReceiptId(receiptId, mapper = { id, itemId, name, count, price, unit, categoryId,categoryName,categoryColor, date, currency ->
                ItemEntity(id, itemId,name,count,price,unit, ItemCategoryEntity(categoryId, categoryName,categoryColor),date,currency, receiptId)
            }).executeAsList()

            ReceiptEntity(
                id = receipt.id,
                name = receipt.name,
                date = receipt.date,
                currency = receipt.currency,
                sumOfPrice = receipt.sumOfPrice,
                description = receipt.description ?: "",
                imageUri = receipt.imageUri,
                items = items
            )
        }
    }
}