package hu.levente.fazekas.receiptscanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import hu.levente.fazekas.Item
import hu.levente.fazekas.Receipt
import hu.levente.fazekas.database.ReceiptDatabase
import hu.levente.fazekas.receiptscanner.database.Currency
import hu.levente.fazekas.receiptscanner.database.DateAdapter
import hu.levente.fazekas.receiptscanner.database.ItemCategoryEntity
import hu.levente.fazekas.receiptscanner.database.ItemEntity
import hu.levente.fazekas.receiptscanner.database.ReceiptEntity
import hu.levente.fazekas.receiptscanner.database.SqlDelightItemCategoryDataSource
import hu.levente.fazekas.receiptscanner.database.SqlDelightItemDataSource
import hu.levente.fazekas.receiptscanner.database.SqlDelightReceiptDataSource
import hu.levente.fazekas.receiptscanner.database.SqlDelightTagDataSource
import hu.levente.fazekas.receiptscanner.database.TagEntity
import hu.levente.fazekas.receiptscanner.presentation.ReceiptList
import hu.levente.fazekas.receiptscanner.presentation.ReceiptListViewModel
import hu.levente.fazekas.receiptscanner.presentation.SearchBar
import hu.levente.fazekas.receiptscanner.ui.theme.ReceiptScannerTheme
import kotlinx.datetime.Instant
import kotlinx.datetime.Month

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = ReceiptDatabase(
            driver = AndroidSqliteDriver(ReceiptDatabase.Schema, applicationContext, "test.db",
                callback = object : AndroidSqliteDriver.Callback(ReceiptDatabase.Schema) {
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        db.setForeignKeyConstraintsEnabled(true)
                    }
                }
            ),
            ReceiptAdapter = Receipt.Adapter(
                dateAdapter = DateAdapter(),
                currencyAdapter = EnumColumnAdapter()
            ),
            ItemAdapter = Item.Adapter(
                dateAdapter = DateAdapter(),
                currencyAdapter = EnumColumnAdapter()
            )
        )
        val categoryDataSource = SqlDelightItemCategoryDataSource(db)
        val tagDataSource = SqlDelightTagDataSource(db)
//        categoryDataSource.insertCategory(defaultCategory)
//        categoryDataSource.insertCategory(sampleCategory)
        val itemDataSource = SqlDelightItemDataSource(db)
        val receiptDataSource = SqlDelightReceiptDataSource(db, itemDataSource)
//        receiptDataSource.insertReceipt(sampleReceipt)
//        receiptDataSource.insertReceipt(sampleReceipt2)
//        receiptDataSource.insertReceipt(sampleReceipt3)
        val viewModel by viewModels<ReceiptListViewModel> {
            viewModelFactory {
                initializer {
                    val savedStateHandle = createSavedStateHandle()
                    ReceiptListViewModel(
                        receiptDataSource = receiptDataSource,
                        tagDataSource = tagDataSource,
                        savedStateHandle = savedStateHandle
                    )
                }
            }
        }
        setContent {
            ReceiptScannerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val listState = rememberLazyListState()
                    val expandedFab by remember {
                        derivedStateOf {
                            listState.firstVisibleItemIndex == 0
                        }
                    }

                    Scaffold(
                        floatingActionButton = {
                            ExtendedFloatingActionButton(
                                onClick = { tagDataSource.insertTag("Aldi") },
                                expanded = expandedFab,
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add new receipt"
                                    )
                                },
                                text = { Text(text = "Create receipt") },
                                shape = CircleShape
                            )
                        },
                        floatingActionButtonPosition = FabPosition.Center,
                    ) { paddingValues ->
                        Column(
                            modifier = Modifier.padding(paddingValues)
                        ) {
                            val tags by viewModel.tags.collectAsStateWithLifecycle()
                            val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                            val reducedReceipts by viewModel.searchresult.collectAsStateWithLifecycle()
                            SearchBar(
                                searchQuery = searchQuery,
                                tags = tags,
                                onSearchQueryChanged = viewModel::onSearchQueryChanged,
                                onSearchTriggered = {}
                            )
                            ReceiptList(
                                receipts = reducedReceipts,
                                lazyListState = listState,
                            )
                        }

                    }
                }
            }
        }
    }
}

data class Sort(
    val year: Int,
    val month: Month
)

val defaultCategory = ItemCategoryEntity(
    id = 1,
    name = "Not Specified",
    color = 111
)

val sampleCategory = ItemCategoryEntity(
    id = 2,
    name = "Tejtermék",
    color = 789
)
val sampleItems = listOf(
    ItemEntity(
        id = 1,
        itemId = 1,
        name = "Tej",
        quantity = 3,
        price = 398.0,
        unit = "L",
        category = sampleCategory,
        date = Instant.fromEpochSeconds(3),
        currency = Currency.HUF,
        receiptId = 1
    ),
    ItemEntity(
        id = 2,
        itemId = 1,
        name = "Tej",
        quantity = 5,
        price = 468.0,
        unit = "L",
        category = defaultCategory,
        date = Instant.fromEpochSeconds(2),
        currency = Currency.HUF,
        receiptId = 1
    ),
    ItemEntity(
        id = 3,
        itemId = 2,
        name = "Sajt",
        quantity = 2,
        price = 793.0,
        unit = "kg",
        category = sampleCategory,
        date = Instant.fromEpochSeconds(1),
        currency = Currency.HUF,
        receiptId = 1
    )
)

val sampleTag = TagEntity(
    id = 1,
    name = "Auchan"
)

val sampleReceipt = ReceiptEntity(
    id = 1,
    name = "Auchan",
    date = Instant.fromEpochSeconds(1695717864),
    currency = Currency.HUF,
    sumOfPrice = 5987,
    description = "Egy példa blokk",
    imageUri = "",
    tags = listOf(sampleTag),
    items = sampleItems
)

val sampleReceipt2 = ReceiptEntity(
    id = 2,
    name = "Aldi",
    date = Instant.fromEpochSeconds(1690848000),
    currency = Currency.HUF,
    sumOfPrice = 5987,
    description = "Egy példa blokk",
    imageUri = "",
    tags = listOf(sampleTag),
    items = sampleItems
)

val sampleReceipt3 = ReceiptEntity(
    id = 3,
    name = "Lidl",
    date = Instant.fromEpochSeconds(1659312000),
    currency = Currency.HUF,
    sumOfPrice = 5987,
    description = "Egy példa blokk",
    imageUri = "",
    tags = listOf(sampleTag),
    items = sampleItems
)