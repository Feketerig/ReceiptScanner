package hu.levente.fazekas.receiptscanner.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.levente.fazekas.receiptscanner.Sort
import hu.levente.fazekas.receiptscanner.database.SqlDelightReceiptDataSource
import hu.levente.fazekas.receiptscanner.database.SqlDelightTagDataSource
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ReceiptListViewModel(
    receiptDataSource: SqlDelightReceiptDataSource,
    tagDataSource: SqlDelightTagDataSource,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    val searchQuery = savedStateHandle.getStateFlow(key = "searchQuery", initialValue = "")

    val searchresult = receiptDataSource.selectAllReducedReceiptFlow().map {
        it.groupBy {
            Sort(
                it.date.toLocalDateTime(TimeZone.currentSystemDefault()).year,
                it.date.toLocalDateTime(
                    TimeZone.currentSystemDefault()
                ).month
            )
        }
            .toSortedMap(compareByDescending<Sort> { it.year }.thenByDescending { it.month })
            .map {
                Category(
                    headerText = it.key.year.toString() + " " + it.key.month.name,
                    items = it.value
                )
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val tags = tagDataSource.selectAllTag().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    fun onSearchQueryChanged(query: String) {
        savedStateHandle["searchQuery"] = query
    }
}