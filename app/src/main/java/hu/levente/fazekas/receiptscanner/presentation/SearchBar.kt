package hu.levente.fazekas.receiptscanner.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
    selectedTags: List<TagEntity>,
    onSearchQueryChanged: (String) -> Unit,
    onTagClicked: (TagEntity) -> Unit,
    onAnalyticsClicked: () -> Unit
) {
    var isExpended by rememberSaveable {
        mutableStateOf(false)
    }
    Column {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .onFocusChanged {
                    isExpended = it.hasFocus
                }
        ) {
            SearchTextField(
                onSearchQueryChanged = onSearchQueryChanged,
                searchQuery = searchQuery,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { onAnalyticsClicked() }) {
                Icon(imageVector = Icons.Default.Analytics, contentDescription = null)
            }
        }
        if (isExpended) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp),
                mainAxisSpacing = 8.dp,
                mainAxisSize = SizeMode.Wrap
            ) {
                tags.forEach { tag ->
                    FilterChip(
                        onClick = { onTagClicked(tag) },
                        label = { Text(text = tag.name) },
                        shape = CircleShape,
                        selected = selectedTags.contains(tag),
                        leadingIcon = if (selectedTags.contains(tag)) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                )
                            }
                        } else {
                            null
                        }
                    )
                }
            }
        }
    }
}