package hu.levente.fazekas.receiptscanner.presentation

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.RadioButton
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
//    var sortBy by rememberSaveable {
//        mutableIntStateOf(0)
//    }
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
                searchQuery = searchQuery
            )
            IconButton(onClick = { onAnalyticsClicked() }) {
                Icon(imageVector = Icons.Default.Analytics, contentDescription = null)
            }
        }
        if (isExpended) {
//            Row {
//                Text(text = "Sort By: ")
//                Spacer(modifier = Modifier.width(8.dp))
//                RadioButtonWithText(text = "Date", isSelected = sortBy == 0) {
//                    sortBy = 0
//                }
//                Spacer(modifier = Modifier.width(8.dp))
//                RadioButtonWithText(text = "Name", isSelected = sortBy == 1) {
//                    sortBy = 1
//                }
//            }
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

//val donutChartData = PieChartData(
//    slices = listOf(
//        PieChartData.Slice("HP", 15f, Color(0xFF5F0A87)),
//        PieChartData.Slice("Dell", 30f, Color(0xFF20BF55)),
//        PieChartData.Slice("Lenovo", 40f,  Color(0xFFEC9F05)),
//        PieChartData.Slice("Asus", 10f, Color(0xFFF53844))
//    ),
//    plotType = PlotType.Donut
//)
//val donutChartConfig = PieChartConfig(
////                percentVisible = true,
////                percentageFontSize = 42.sp,
//    strokeWidth = 120f,
////                percentColor = Color.Black,
//    activeSliceAlpha = .9f,
//    isAnimationEnable = true
//)
//DonutPieChart(
//modifier = Modifier
//.fillMaxWidth()
//.height(500.dp),
//donutChartData,
//donutChartConfig
//)