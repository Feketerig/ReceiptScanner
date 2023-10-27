package hu.levente.fazekas.receiptscanner.presentation.create_edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.levente.fazekas.currency.Currency
import hu.levente.fazekas.receiptscanner.database.ItemEntity
import hu.levente.fazekas.receiptscanner.database.TagEntity
import hu.levente.fazekas.receiptscanner.defaultCategory
import hu.levente.fazekas.receiptscanner.sampleCategory
import hu.levente.fazekas.receiptscanner.sampleItems
import hu.levente.fazekas.receiptscanner.sampleReceipt
import hu.levente.fazekas.receiptscanner.sampleTag


@Composable
fun CreateEditReceiptScreen() {
    var openDatePickerDialog by remember { mutableStateOf(false) }
    var openCurrencyPickerDialog by remember { mutableStateOf(false) }
    var openTagPickerDialog by remember { mutableStateOf(false) }
    var openItemPickerDialog by remember { mutableStateOf(false) }
    var openNewTagDialog by remember { mutableStateOf(false) }
    var openItemDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<ItemEntity?>(null) }

    if (openCurrencyPickerDialog){
        CurrencyPicker(
            selectedCurrency = Currency.HUF,
            onHideDialog = { openCurrencyPickerDialog = false },
            onCurrencyClick = {  }
        )
    }else if (openTagPickerDialog){
        TagPicker(
            onHideDialog = { openTagPickerDialog = false },
            allTags = listOf(sampleTag, TagEntity(2, "Aldi")),
            selectedTags = listOf(sampleTag),
            onTagSelected = {  },
            onNewTag = { openNewTagDialog = true }
        )
    }else if (openItemDialog){
        CreateEditItem(
            item = selectedItem,
            currency = Currency.HUF,
            categories = listOf(defaultCategory, sampleCategory),
            onSaveItem = {},
            onDeleteItem = {},
            onHideDialog = { openItemDialog = false },
        )
    }else if (openItemPickerDialog){
        ItemPicker(
            onHideDialog = { openItemPickerDialog = false },
            items = sampleItems,
            onItemSelected = {
                selectedItem = it
                openItemDialog = true
            },
            onNewItem = {
                selectedItem = null
                openItemDialog = true
            }
        )
    }else {
        CreateEditReceipt(
            state = CreateEditState(
                receipt = sampleReceipt.copy(
                    tags = listOf(sampleTag, TagEntity(2, "Aldi"))
                )
            ),
            onBack = {},
            onSave = {},
            onNameTextFieldChanged = {},
            onDateClick = { openDatePickerDialog = true },
            onCurrencyClick = { openCurrencyPickerDialog = true },
            onTotalTextFieldChanged = {},
            onTagsClick = { openTagPickerDialog = true },
            onItemsClick = { openItemPickerDialog = true },
            onDescriptionTextFieldChanged = {},
        )
    }

    if (openNewTagDialog){
        AddNewTag(
            onHideDialog = { openNewTagDialog = false },
            onAddNewTag = {  }
        )
    }

    if (openDatePickerDialog){
        DatePicker(
            onHideDialog = { openDatePickerDialog = false },
            onSelectedDate = {  }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    onHideDialog: () -> Unit,
    onSelectedDate: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = {
            onHideDialog()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { onSelectedDate(it) }
                    onHideDialog()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onHideDialog()
                }
            ) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyPicker(
    selectedCurrency: Currency,
    onHideDialog: () -> Unit,
    onCurrencyClick: (Currency) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Currency",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onHideDialog() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },

            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(Currency.entries) { currency ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onCurrencyClick(currency)
                            onHideDialog()
                        }
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(currency.icon),
                            contentDescription = currency.fullName,
                            modifier = Modifier.size(50.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = currency.fullName)
                    }
                    if (currency == selectedCurrency) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.Green,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagPicker(
    onHideDialog: () -> Unit,
    allTags: List<TagEntity>,
    selectedTags: List<TagEntity>,
    onTagSelected: (Boolean) -> Unit,
    onNewTag: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Tags",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onHideDialog() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    onNewTag()
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add new tag"
                    )
                },
                text = { Text(text = "Create a tag") },
                shape = CircleShape
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(allTags) { tag ->
                val isSelected = selectedTags.contains(tag)
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onTagSelected(!isSelected)
                        }
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                ) {
                    Text(text = tag.name)
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onTagSelected(!isSelected) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemPicker(
    onHideDialog: () -> Unit,
    items: List<ItemEntity>,
    onItemSelected: (ItemEntity?) -> Unit,
    onNewItem: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Items",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onHideDialog() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                          onNewItem()
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add new item"
                    )
                },
                text = { Text(text = "Add item") },
                shape = CircleShape
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(items) { item ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onItemSelected(item)
                        }
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = "Shopping Cart",
                            modifier = Modifier.size(30.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = item.name
                            )
                            Text(
                                text = item.quantity.toString() + " x " + item.price + item.currency.symbol,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Light,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                    Text(text = (item.quantity * item.price).toString() + item.currency.symbol)
                }
            }
        }
    }
}

@Composable
fun AddNewTag(
    onHideDialog: () -> Unit,
    onAddNewTag: (String) -> Unit
) {
    var tagName by remember {
        mutableStateOf("")
    }
    AlertDialog(
        onDismissRequest = {
            onHideDialog()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onAddNewTag(tagName)
                    onHideDialog()
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onHideDialog()
                }
            ) {
                Text("Cancel")
            }
        },
        title = {
            Text(text = "Add new tag")
        },
        text = {
            TextField(
                value = tagName,
                onValueChange = { tagName = it },
                placeholder = {
                    Text(text = "Enter tag name")
                }
            )
        }
    )
}

@Preview
@Composable
fun DatePickerPreview() {
    DatePicker(
        onHideDialog = {  },
        onSelectedDate = {  }
    )
}

@Preview(showBackground = true)
@Composable
fun CurrencyPickerPreview() {
    CurrencyPicker (
        selectedCurrency = Currency.HUF,
        onHideDialog = { },
        onCurrencyClick = { }
    )
}

@Preview
@Composable
fun TagPickerPreview() {
    TagPicker(
        onHideDialog = {  },
        allTags = listOf(sampleTag, TagEntity(2, "Aldi")),
        selectedTags = listOf(sampleTag),
        onTagSelected = { },
        onNewTag = { }
    )
}

@Preview
@Composable
fun ItemPickerPreview() {
    ItemPicker(
        onHideDialog = {  },
        items = sampleItems,
        onItemSelected = { },
        onNewItem = { }
    )
}

@Preview
@Composable
fun AddNewTagPreview() {
    AddNewTag(
        onHideDialog = { },
        onAddNewTag = { }
    )
}