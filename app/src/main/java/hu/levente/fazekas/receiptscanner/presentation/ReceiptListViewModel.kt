package hu.levente.fazekas.receiptscanner.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.levente.fazekas.receiptscanner.database.SqlDelightReceiptDataSource
import hu.levente.fazekas.receiptscanner.database.SqlDelightTagDataSource
import hu.levente.fazekas.receiptscanner.database.TagEntity
import hu.levente.fazekas.receiptscanner.domain.ReceiptSearchUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReceiptListViewModel(
    receiptDataSource: SqlDelightReceiptDataSource,
    tagDataSource: SqlDelightTagDataSource,
    receiptSearchUseCase: ReceiptSearchUseCase,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    val searchQuery = savedStateHandle.getStateFlow(key = "searchQuery", initialValue = "")

    val selectedTags = MutableStateFlow(listOf<TagEntity>())

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResult = selectedTags.combine(searchQuery) { tags, query ->
        receiptSearchUseCase.get(query, tags)
    }
        .flattenMerge()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val tags = searchQuery.flatMapLatest{
        tagDataSource.selectWithFilter(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    fun onSearchQueryChanged(query: String) {
        savedStateHandle["searchQuery"] = query
    }

    fun onTagChange(newTag: TagEntity) {
        viewModelScope.launch {
            if (selectedTags.value.contains(newTag)) {
                selectedTags.emit(selectedTags.value.minus(newTag))
            } else {
                selectedTags.emit(selectedTags.value.plus(newTag))
            }
        }
    }
}