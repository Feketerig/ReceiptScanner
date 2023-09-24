package hu.levente.fazekas.receiptscanner.data

import hu.levente.fazekas.receiptscanner.database.ReceiptEntity
import hu.levente.fazekas.receiptscanner.database.ReducedReceiptEntity
import kotlinx.coroutines.flow.Flow

interface ReceiptRepository {
    fun insertReceipt(receipt: ReceiptEntity)
    fun selectReceiptById(receiptId: Long):  Flow<ReceiptEntity>
    fun selectAllReducedReceipt(): Flow<List<ReducedReceiptEntity>>
    fun deleteReceipt(receiptId: Long)
    fun updateReceipt(receipt: ReceiptEntity)
}