package hu.levente.fazekas.receiptscanner.navigation

import android.content.Context
import android.graphics.Typeface
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import hu.levente.fazekas.receiptscanner.database.SqlDelightReceiptDataSource
import hu.levente.fazekas.receiptscanner.presentation.ReceiptChart
import hu.levente.fazekas.receiptscanner.presentation.ReceiptList
import hu.levente.fazekas.receiptscanner.presentation.ReceiptListViewModel
import hu.levente.fazekas.receiptscanner.presentation.SearchBar
import hu.levente.fazekas.receiptscanner.presentation.random

@Composable
fun NavHost(
    navController: NavHostController,
    listState: LazyListState,
    viewModel: ReceiptListViewModel,
    receiptDataSource: SqlDelightReceiptDataSource,
    context: Context,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "list",
        modifier = modifier
    ){
        composable(route = "list") {
            val tags by viewModel.tags.collectAsStateWithLifecycle()
            val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
            val reducedReceipts by viewModel.searchResult.collectAsStateWithLifecycle()
            val selectedTags by viewModel.selectedTags.collectAsStateWithLifecycle()
            Column(
            ) {
                SearchBar(
                    searchQuery = searchQuery,
                    tags = tags,
                    selectedTags = selectedTags,
                    onSearchQueryChanged = viewModel::onSearchQueryChanged,
                    onTagClicked = viewModel::onTagChange,
                    onAnalyticsClicked = { navController.navigate("receiptChart") }
                )
                ReceiptList(
                    receipts = reducedReceipts,
                    lazyListState = listState,
                )
            }
        }

        composable(route = "receiptChart"){
            val receipts = receiptDataSource.db.receiptQueries.selectByName().executeAsList()
            val colors by remember{
                mutableStateOf(
                    receipts.map {
                    Color.random()
                }
                )
            }
            val slices = receipts.mapIndexed { index, it ->
                PieChartData.Slice(it.name, it.sumOfPrice?.toFloat()!!, colors[index])
            }
            val data = PieChartData(slices = slices, plotType = PlotType.Donut)
            val pieChartConfig =
                PieChartConfig(
                    labelVisible = true,
                    strokeWidth = 120f,
                    labelColor = Color.Black,
                    activeSliceAlpha = .9f,
                    isEllipsizeEnabled = true,
                    labelTypeface = Typeface.defaultFromStyle(Typeface.BOLD),
                    isAnimationEnable = true,
                    chartPadding = 25,
                    labelFontSize = 42.sp,
                    isSumVisible = true,
                    labelType = PieChartConfig.LabelType.VALUE
                )
            ReceiptChart(context, data, pieChartConfig)
        }

    }
}