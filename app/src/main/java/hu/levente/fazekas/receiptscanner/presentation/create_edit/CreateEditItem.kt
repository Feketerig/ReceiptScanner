package hu.levente.fazekas.receiptscanner.presentation.create_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.levente.fazekas.currency.Currency
import hu.levente.fazekas.receiptscanner.R
import hu.levente.fazekas.receiptscanner.database.ItemCategoryEntity
import hu.levente.fazekas.receiptscanner.database.ItemEntity
import hu.levente.fazekas.receiptscanner.defaultCategory
import hu.levente.fazekas.receiptscanner.sampleCategory
import hu.levente.fazekas.receiptscanner.sampleItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditItem(
    item: ItemEntity?,
    currency: Currency,
    categories: List<ItemCategoryEntity>,
    onSaveItem: (ItemEntity) -> Unit,
    onDeleteItem: (Long) -> Unit,
    onHideDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var itemEntity by remember {
        mutableStateOf(item ?: ItemEntity())
    }
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            IconButton(
                onClick = { onHideDialog() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Button(onClick = { onSaveItem(itemEntity) }) {
                Text(text = "Save")
            }
        }

        TextField(
            value = itemEntity.name,
            onValueChange = { itemEntity = itemEntity.copy(name = it) },
            textStyle = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            ),
            maxLines = 1,
            singleLine = true,
            placeholder = {
                Text(
                    text = "Item name",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            )
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.tag),
                    contentDescription = "Icon"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Price")
            }
            TextField(
                value = itemEntity.price.toString(),
                onValueChange = { itemEntity = itemEntity.copy(price = it.toDouble()) },
                maxLines = 1,
                textStyle = TextStyle(
                    textAlign = TextAlign.End
                ),
                trailingIcon = { Text(text = currency.symbol) },
                singleLine = true,
                placeholder = {
                    Text(text = "Price")
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp)
        ) {
            Text(text = "Quantity")
            TextField(
                value = itemEntity.quantity.toString(),
                onValueChange = { itemEntity = itemEntity.copy(quantity = it.toLong()) },
                maxLines = 1,
                textStyle = TextStyle(
                    textAlign = TextAlign.End
                ),
                trailingIcon = { Text(text = currency.symbol) },
                singleLine = true,
                placeholder = {
                    Text(text = "Quantity")
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp)
        ) {
            Text(text = "Total")
            TextField(
                value = (itemEntity.quantity * itemEntity.price).toString(),
                onValueChange = { itemEntity = itemEntity.copy(name = it) },
                maxLines = 1,
                textStyle = TextStyle(
                    textAlign = TextAlign.End
                ),
                trailingIcon = { Text(text = currency.symbol) },
                singleLine = true,
                placeholder = {
                    Text(text = "Total")
                },
                enabled = false,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    disabledContainerColor = MaterialTheme.colorScheme.background,
                    disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp)
        ) {
            Text(text = "Category")

            var expanded by remember { mutableStateOf(false) }
            var selectedOptionText by remember { mutableStateOf(item?.category?.name ?: categories[0].name) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                TextField(
                    modifier = Modifier.menuAnchor(),
                    readOnly = true,
                    value = selectedOptionText,
                    textStyle = TextStyle(
                        textAlign = TextAlign.End
                    ),
                    onValueChange = {},
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        disabledContainerColor = MaterialTheme.colorScheme.background,
                        disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedOptionText = category.name
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }

        item?.let {
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Button(
                onClick = {
                    onDeleteItem(it.id)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Delete")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditItemPreview() {
    CreateEditItem(
        item = sampleItems[0],
        currency = sampleItems[0].currency,
        categories = listOf(defaultCategory, sampleCategory),
        onSaveItem = { },
        onDeleteItem = { },
        onHideDialog = { }
    )
}

@Preview(showBackground = true)
@Composable
fun CreateItemPreview() {
    CreateEditItem(
        item = null,
        currency = sampleItems[0].currency,
        categories = listOf(defaultCategory, sampleCategory),
        onSaveItem = { },
        onDeleteItem = { },
        onHideDialog = { }
    )
}