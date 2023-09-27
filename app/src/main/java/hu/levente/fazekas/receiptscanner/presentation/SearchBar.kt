package hu.levente.fazekas.receiptscanner.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(

) {
    var isExpended by rememberSaveable {
        mutableStateOf(false)
    }
    var sortBy by rememberSaveable {
        mutableStateOf(0)
    }
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchTextField(
                onSearchQueryChanged = {},
                searchQuery = "",
                onSearchTriggered = {}
            )
            IconButton(onClick = { isExpended = !isExpended }) {
                Icon(
                    imageVector =
                    if (isExpended) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                    contentDescription = "Expand search bar"
                )
            }
        }
        if (isExpended) {
            Row {
                Text(text = "Sort By: ")
                Spacer(modifier = Modifier.width(8.dp))
                RadioButtonWithText(text = "Date", isSelected = sortBy == 0) {
                    sortBy = 0
                }
                Spacer(modifier = Modifier.width(8.dp))
                RadioButtonWithText(text = "Name", isSelected = sortBy == 1) {
                    sortBy = 1
                }
            }
        }
    }
}

@Composable
fun RadioButtonWithText(
    text: String,
    isSelected: Boolean,
    onClicked: () -> Unit
) {
    Row(
        modifier = Modifier.clickable {
            onClicked()
        }
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null
        )
        Text(text = text)
    }

}