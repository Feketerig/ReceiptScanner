package hu.levente.fazekas.receiptscanner.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import hu.levente.fazekas.receiptscanner.database.Currency
import hu.levente.fazekas.receiptscanner.database.ReducedReceiptEntity
import hu.levente.fazekas.receiptscanner.database.TagEntity
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun ReceiptList(
    receipts: List<ReceiptCategory>,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    CategorizedLazyColumn(
        receipts = receipts,
        lazyListState = lazyListState,
        modifier = modifier
    )
}

@Composable
private fun CategoryHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryItem(
    receipt: ReducedReceiptEntity,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = { onItemClick() },
        modifier = modifier.padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.Top
                    ) {
                        Text(
                            text = receipt.name,
                            fontSize = 16.sp
                        )
                        Text(
                            text = receipt.date.toLocalDateTime(TimeZone.currentSystemDefault()).date.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 5.dp)
                        )
                    }
                    Text(text = receipt.sumOfPrice.toString() + " " + receipt.currency.symbol)
                }
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    mainAxisSpacing = 8.dp,
                    mainAxisSize = SizeMode.Wrap
                ) {
                    receipt.tags.forEach { tag ->
                        SuggestionChip(
                            onClick = { },
                            label = { Text(text = tag.name) },
                            shape = CircleShape
                        )
                    }
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Left arrow",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategorizedLazyColumn(
    receipts: List<ReceiptCategory>,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = lazyListState,
        modifier = modifier
    ) {
        receipts.forEach { receiptCategory ->
            stickyHeader {
                CategoryHeader(receiptCategory.headerText)
            }
            items(receiptCategory.receipts) { receipt ->
                CategoryItem(
                    receipt = receipt,
                    onItemClick = {}
                )
            }
        }
    }
}

data class ReceiptCategory(
    val headerText: String,
    val receipts: List<ReducedReceiptEntity>
)

@Preview
@Composable
fun ReceiptListPreview(){
    val listState = rememberLazyListState()
    ReceiptList(
        lazyListState = listState,
        receipts = listOf(
            ReceiptCategory(
                headerText = "2023 Szeptember",
                receipts = listOf(
                    ReducedReceiptEntity(
                        id = 1,
                        name = "Auchan",
                        date = Instant.fromEpochSeconds(1695717864),
                        currency = Currency.HUF,
                        sumOfPrice = 1235,
                        tags = listOf(TagEntity(1, "Auchan"))
                    ),
                    ReducedReceiptEntity(
                        id = 1,
                        name = "Aldi",
                        date = Instant.fromEpochSeconds(1695650000),
                        currency = Currency.HUF,
                        sumOfPrice = 14849,
                        tags = listOf(TagEntity(1, "Aldi"))
                    )
                )
            ),
            ReceiptCategory(
                headerText = "2023 Augusztus",
                receipts = listOf(
                    ReducedReceiptEntity(
                        id = 1,
                        name = "Auchan",
                        date = Instant.fromEpochSeconds(1690848000),
                        currency = Currency.HUF,
                        sumOfPrice = 512,
                        tags = emptyList()
                    ),
                    ReducedReceiptEntity(
                        id = 1,
                        name = "Aldi",
                        date = Instant.fromEpochSeconds(1690848000),
                        currency = Currency.HUF,
                        sumOfPrice = 456,
                        tags = emptyList()
                    )
                )
            )
        )
    )
}