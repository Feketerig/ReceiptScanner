package hu.levente.fazekas.receiptscanner.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import hu.levente.fazekas.receiptscanner.database.TagEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    searchQuery: String,
    tags: List<TagEntity>,
    onSearchQueryChanged: (String) -> Unit,
    onSearchTriggered: (String) -> Unit
) {
    var isExpended by rememberSaveable {
        mutableStateOf(false)
    }
    var sortBy by rememberSaveable {
        mutableIntStateOf(0)
    }
    Column {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .onFocusChanged {
                    isExpended = it.hasFocus
                }
        ) {
            SearchTextField(
                onSearchQueryChanged = onSearchQueryChanged,
                searchQuery = searchQuery,
                onSearchTriggered = onSearchTriggered
            )
//            IconButton(onClick = { isExpended = !isExpended }) {
//                Icon(
//                    imageVector =
//                    if (isExpended) {
//                        Icons.Default.KeyboardArrowUp
//                    } else {
//                        Icons.Default.KeyboardArrowDown
//                    },
//                    contentDescription = "Expand search bar"
//                )
//            }
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
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp),
                mainAxisSpacing = 8.dp,
                mainAxisSize = SizeMode.Wrap
            ) {
                tags.forEach { tag ->
                    FilterChip(
                        onClick = { },
                        label = { Text(text = tag.name) },
                        shape = CircleShape,
                        selected = true
                    )
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