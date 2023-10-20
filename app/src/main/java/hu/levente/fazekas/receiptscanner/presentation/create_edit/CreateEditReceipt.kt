package hu.levente.fazekas.receiptscanner.presentation.create_edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import hu.levente.fazekas.receiptscanner.database.ReceiptEntity
import hu.levente.fazekas.receiptscanner.database.TagEntity
import hu.levente.fazekas.receiptscanner.sampleReceipt
import hu.levente.fazekas.receiptscanner.sampleTag
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun CreateEditReceipt(
    state: CreateEditState,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onNameTextFieldChanged: (String) -> Unit,
    onDateClick: () -> Unit,
    onCurrencyClick: () -> Unit,
    onTotalTextFieldChanged: (String) -> Unit,
    onTagsClick: () -> Unit,
    onItemsClick: () -> Unit,
    onDescriptionTextFieldChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            IconButton(
                onClick = { onBack() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Button(onClick = { onSave() }) {
                Text(text = "Save")
            }
        }
        //Name
        TextField(
            value = state.receipt.name,
            onValueChange = { onNameTextFieldChanged(it) },
            textStyle = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            ),
            maxLines = 1,
            singleLine = true,
            placeholder = {
                Text(
                    text = "Merchant name",
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
        //Image
        AsyncImage(
            model = null,
            contentDescription = "Image of the receipt",
            placeholder = null,
            modifier = Modifier.height(300.dp)
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        //Date
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onDateClick() }
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "Date"
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = state.receipt.date.toLocalDateTime(TimeZone.currentSystemDefault()).date.toString())
        }
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        //Currency + Total sum
        Column {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCurrencyClick() }
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CurrencyExchange,
                    contentDescription = "Currency"
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = state.receipt.currency.name)
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "Total"
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Total: ")
                }

                TextField(
                    value = state.receipt.sumOfPrice.toString(),
                    onValueChange = { onTotalTextFieldChanged(it) },
                    maxLines = 1,
                    textStyle = TextStyle(
                        textAlign = TextAlign.End
                    ),
                    trailingIcon = { Text(text = state.receipt.currency.symbol) },
                    singleLine = true,
                    placeholder = {
                        Text(text = "Total sum")
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
        }
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        //Tags
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTagsClick() }
                .padding(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Tag,
                    contentDescription = "Tags"
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Tags")
            }
            Text(
                text =
                if (state.receipt.tags.isEmpty())
                    "None"
                else
                    state.receipt.tags.joinToString(", ") { it.name })
        }
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        //Items
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemsClick() }
                .padding(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Items"
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Items")
            }
            Text(
                text =
                if (state.receipt.items.isEmpty())
                    "None"
                else
                    state.receipt.items.size.toString() + " items"
            )
        }
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        //Description
        TextField(
            value = state.receipt.description,
            onValueChange = { onDescriptionTextFieldChanged(it) },
            placeholder = { Text(text = "Description") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Comment,
                    contentDescription = "Description"
                )
            }
        )
    }

}

@Preview(showBackground = true)
@Composable
fun CreateEditReceiptPreview() {
    CreateEditReceipt(
        state = CreateEditState(
            receipt = sampleReceipt.copy(
                tags = listOf(sampleTag, TagEntity(2, "Aldi"))
            )
        ),
        onBack = {},
        onSave = {},
        onNameTextFieldChanged = {},
        onDateClick = {},
        onCurrencyClick = {},
        onTotalTextFieldChanged = {},
        onTagsClick = {},
        onItemsClick = {},
        onDescriptionTextFieldChanged = {},
    )
}

@Preview(showBackground = true)
@Composable
fun EmptyCreateEditReceiptPreview() {
    CreateEditReceipt(
        state = CreateEditState(
            receipt = ReceiptEntity()
        ),
        onBack = {},
        onSave = {},
        onNameTextFieldChanged = {},
        onDateClick = {},
        onCurrencyClick = {},
        onTotalTextFieldChanged = {},
        onTagsClick = {},
        onItemsClick = {},
        onDescriptionTextFieldChanged = {},
    )
}