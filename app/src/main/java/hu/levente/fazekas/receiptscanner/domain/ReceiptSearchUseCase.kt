package hu.levente.fazekas.receiptscanner.domain

import hu.levente.fazekas.receiptscanner.Sort
import hu.levente.fazekas.receiptscanner.database.ReducedReceiptEntity
import hu.levente.fazekas.receiptscanner.database.SqlDelightReceiptDataSource
import hu.levente.fazekas.receiptscanner.database.TagEntity
import hu.levente.fazekas.receiptscanner.presentation.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ReceiptSearchUseCase(
    private val receiptDataSource: SqlDelightReceiptDataSource
) {
    fun get(searchQuery: String, tags: List<TagEntity>): Flow<List<Category>>{
        return receiptDataSource
            .selectAllReducedReceiptFlow(searchQuery, tags)
            .mapToCategory()
    }

    private fun Flow<List<ReducedReceiptEntity>>.mapToCategory(): Flow<List<Category>>{
        return this.map {
            it.groupBy {
                Sort(
                    it.date.toLocalDateTime(TimeZone.currentSystemDefault()).year,
                    it.date.toLocalDateTime(TimeZone.currentSystemDefault()).month,
                )
            }
                .toSortedMap(compareByDescending<Sort> { it.year }.thenByDescending { it.month })
                .map {
                    Category(
                        headerText = it.key.year.toString() + " " + it.key.month.name,
                        items = it.value
                    )
                }
        }
    }
}