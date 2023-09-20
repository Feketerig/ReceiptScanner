package hu.levente.fazekas.data

import hu.levente.fazekas.Receipt
import kotlinx.coroutines.flow.Flow

interface ReceiptRepository {

    fun getReceipts(): Flow<Receipt>
}