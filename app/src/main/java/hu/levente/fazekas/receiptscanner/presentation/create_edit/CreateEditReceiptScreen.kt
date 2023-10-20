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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.levente.fazekas.currency.Currency
import hu.levente.fazekas.receiptscanner.database.TagEntity
import hu.levente.fazekas.receiptscanner.sampleReceipt
import hu.levente.fazekas.receiptscanner.sampleTag


@Composable
fun CreateEditReceiptScreen() {
    var openDatePickerDialog by remember { mutableStateOf(false) }
    var openCurrencyPickerDialog by remember { mutableStateOf(false) }
    var openTagPickerDialog by remember { mutableStateOf(false) }
    var openItemPickerDialog by remember { mutableStateOf(false) }

    if (openDatePickerDialog){
        DatePicker(
            onHideDialog = { openDatePickerDialog = false },
            onSelectedDate = {  }
        )
    }

    if (openCurrencyPickerDialog){
        CurrencyPicker(
            selectedCurrency = Currency.HUF,
            onHideDialog = { openCurrencyPickerDialog = false },
            onCurrencyClick = {
                openCurrencyPickerDialog = false
            }
        )
    }

    if (openTagPickerDialog){
        TagPicker(
            onHideDialog = { openTagPickerDialog = false },
        )
    }

    if (openItemPickerDialog){
        ItemPicker(
            onHideDialog = { openItemPickerDialog = false },
        )
    }

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
        onTagsClick = {},
        onItemsClick = {},
        onDescriptionTextFieldChanged = {},
    )
    
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

@Composable
fun CurrencyPicker(
    selectedCurrency: Currency,
    onHideDialog: () -> Unit,
    onCurrencyClick: (Currency) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            IconButton(onClick = { onHideDialog() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "Currency",
                textAlign = TextAlign.Center,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
        }
        LazyColumn {
            items(Currency.entries){ currency ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onCurrencyClick(currency)
                        }.padding(vertical = 4.dp, horizontal = 8.dp)
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
                    if (currency == selectedCurrency){
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

@Composable
fun TagPicker(
    onHideDialog: () -> Unit,
) {

}

@Composable
fun ItemPicker(
    onHideDialog: () -> Unit,
) {

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
    )
}

@Preview
@Composable
fun ItemPickerPreview() {
    ItemPicker(
        onHideDialog = {  },
    )
}