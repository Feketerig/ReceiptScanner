package hu.levente.fazekas.receiptscanner.presentation.create_edit

import hu.levente.fazekas.receiptscanner.database.ReceiptEntity

data class CreateEditState(
    val receipt: ReceiptEntity
)
