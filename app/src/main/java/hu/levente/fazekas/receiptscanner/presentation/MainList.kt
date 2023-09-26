package hu.levente.fazekas.receiptscanner.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
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

@Composable
fun ReceiptList(
    categories: List<Category>,
    modifier: Modifier = Modifier
) {
    CategorizedLazyColumn(
        categories = categories
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun CategoryItem(
    receipt: ReducedReceiptEntity,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = { /*TODO*/ },
        modifier = Modifier.padding(8.dp)
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = receipt.name,
                fontSize = 14.sp,
                modifier = modifier

                    .padding(16.dp)
            )
            Text(text = receipt.sumOfPrice.toString() + " " + receipt.currency.symbol)
        }
        FlowRow(modifier = Modifier.fillMaxWidth(), mainAxisSpacing = 8.dp, mainAxisSize = SizeMode.Wrap) {
            receipt.tags.forEach { tag ->
                SuggestionChip(
                    onClick = {  },
                    label = { Text(text = tag.name) },
                    shape = CircleShape
                )
            }
        }
    }
}

data class Category(
    val name: String,
    val items: List<ReducedReceiptEntity>
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategorizedLazyColumn(
    categories: List<Category>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        categories.forEach { category ->
            stickyHeader {
                CategoryHeader(category.name)
            }
            items(category.items) { text ->
                CategoryItem(text)
            }
        }
    }
}

data class ReceiptListState(
    val reducedReceipts: List<ReducedReceiptEntity> = emptyList()
)

@Preview
@Composable
fun ReceiptListPreview(){
    ReceiptList(
        categories = listOf(
            Category(
                name = "Szeptember",
                items = listOf(
                    ReducedReceiptEntity(
                        id = 1,
                        name = "Auchan",
                        date = Instant.fromEpochSeconds(1),
                        currency = Currency.HUF,
                        sumOfPrice = 1235,
                        tags = listOf(TagEntity(1, "Auchan"))
                    ),
                    ReducedReceiptEntity(
                        id = 1,
                        name = "Aldi",
                        date = Instant.fromEpochSeconds(1),
                        currency = Currency.HUF,
                        sumOfPrice = 14849,
                        tags = listOf(TagEntity(1, "Aldi"))
                    )
                )
            ),
            Category(
                name = "Augusztus",
                items = listOf(
                    ReducedReceiptEntity(
                        id = 1,
                        name = "Auchan",
                        date = Instant.fromEpochSeconds(1),
                        currency = Currency.HUF,
                        sumOfPrice = 512,
                        tags = emptyList()
                    ),
                    ReducedReceiptEntity(
                        id = 1,
                        name = "Aldi",
                        date = Instant.fromEpochSeconds(1),
                        currency = Currency.HUF,
                        sumOfPrice = 456,
                        tags = emptyList()
                    )
                )
            )
        )
    )
}