package hu.levente.fazekas.receiptscanner.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.yml.charts.common.components.Legends
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import kotlin.random.Random

@Composable
fun ReceiptChart(
    context: Context,
    data: PieChartData,
    pieChartConfig: PieChartConfig
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Legends(legendsConfig = DataUtils.getLegendsConfigFromPieChartData(pieChartData = data, 2))
        DonutPieChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            data,
            pieChartConfig
        ) { slice ->
            Toast.makeText(context, "${slice.label}: ${slice.value} Ft" , Toast.LENGTH_SHORT).show()
        }
    }
}

fun Color.Companion.random() : Color {
    val red = Random.nextInt(256)
    val green = Random.nextInt(256)
    val blue = Random.nextInt(256)
    return Color(red, green, blue)
}