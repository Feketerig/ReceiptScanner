package hu.levente.fazekas.receiptscanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import hu.levente.fazekas.Item
import hu.levente.fazekas.Receipt
import hu.levente.fazekas.currency.Currency
import hu.levente.fazekas.database.ReceiptDatabase
import hu.levente.fazekas.receiptscanner.database.DateAdapter
import hu.levente.fazekas.receiptscanner.database.ItemCategoryEntity
import hu.levente.fazekas.receiptscanner.database.ItemEntity
import hu.levente.fazekas.receiptscanner.database.ReceiptEntity
import hu.levente.fazekas.receiptscanner.database.SqlDelightItemCategoryDataSource
import hu.levente.fazekas.receiptscanner.database.SqlDelightItemDataSource
import hu.levente.fazekas.receiptscanner.database.SqlDelightReceiptDataSource
import hu.levente.fazekas.receiptscanner.database.SqlDelightTagDataSource
import hu.levente.fazekas.receiptscanner.database.TagEntity
import hu.levente.fazekas.receiptscanner.domain.ReceiptSearchUseCase
import hu.levente.fazekas.receiptscanner.navigation.TopLevelDestination
import hu.levente.fazekas.receiptscanner.presentation.ReceiptListViewModel
import hu.levente.fazekas.receiptscanner.presentation.create_edit.CreateEditReceiptScreen
import hu.levente.fazekas.receiptscanner.ui.theme.ReceiptScannerTheme
import kotlinx.datetime.Instant
import kotlinx.datetime.Month

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
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
//        receiptDataSource.insertReceipt(sampleReceipt4)
//        receiptDataSource.insertReceipt(sampleReceipt5)
        val receiptSearchUseCase = ReceiptSearchUseCase(receiptDataSource)
        val receipts = db.receiptQueries.selectByTag().executeAsList()
        val viewModel by viewModels<ReceiptListViewModel> {
            viewModelFactory {
                initializer {
                    val savedStateHandle = createSavedStateHandle()
                    ReceiptListViewModel(
                        receiptDataSource = receiptDataSource,
                        tagDataSource = tagDataSource,
                        receiptSearchUseCase = receiptSearchUseCase,
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
                    CreateEditReceiptScreen()
//                    val listState = rememberLazyListState()
//                    val expandedFab by remember {
//                        derivedStateOf {
//                            listState.firstVisibleItemIndex == 0
//                        }
//                    }
//                    val navController = rememberNavController()
//
//                    Scaffold(
//                        floatingActionButton = {
//                            ExtendedFloatingActionButton(
//                                onClick = {
//
//                                },
//                                expanded = expandedFab,
//                                icon = {
//                                    Icon(
//                                        imageVector = Icons.Default.Add,
//                                        contentDescription = "Add new receipt"
//                                    )
//                                },
//                                text = { Text(text = "Create receipt") },
//                                shape = CircleShape
//                            )
//                        },
//                        floatingActionButtonPosition = FabPosition.Center,
//                        bottomBar = {
//                            BottomAppBar(
//                                destinations = TopLevelDestination.entries,
//                                onNavigateToDestination = {},
//                                currentDestination = navController.currentDestination
//                            )
//                        }
//                    ) { paddingValues ->
//                        Column(
//                            modifier = Modifier.padding(paddingValues)
//                        ) {
////                            NavHost(
////                                navController = navController,
////                                listState = listState,
////                                viewModel = viewModel,
////                                receiptDataSource = receiptDataSource,
////                                context = applicationContext
////                            )
//                        }
//
//                    }
                }
            }
        }
    }
}

@Composable
fun BottomAppBar(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
) {
    NavigationBar {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        imageVector =
                        if (selected)
                            destination.selectedIcon
                        else
                            destination.unSelectedIcon,
                        contentDescription = destination.title
                    )
                    },
                label = { Text(text = destination.title) }
            )
        }
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false

data class Sort(
    val year: Int,
    val month: Month,
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
        currency =Currency.HUF,
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

val aldiTag = TagEntity(
    id = 2,
    name = "Aldi"
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

val sampleReceipt4 = ReceiptEntity(
    id = 4,
    name = "Aldi1",
    date = Instant.fromEpochSeconds(1695718000),
    currency = Currency.HUF,
    sumOfPrice = 5987,
    description = "Egy példa blokk",
    imageUri = "",
    tags = listOf(sampleTag),
    items = sampleItems
)

val sampleReceipt5 = ReceiptEntity(
    id = 5,
    name = "Lidl1",
    date = Instant.fromEpochSeconds(1695720000),
    currency = Currency.HUF,
    sumOfPrice = 5620,
    description = "Egy példa blokk",
    imageUri = "",
    tags = listOf(aldiTag),
    items = sampleItems
)