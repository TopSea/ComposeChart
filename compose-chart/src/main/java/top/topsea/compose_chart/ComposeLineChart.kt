package top.topsea.compose_chart

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import top.topsea.compose_chart.chart.*

@Composable
fun MultiLineChart(
    chart: LineChart
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CanvasCoordinate(chart = chart)
        chart.lines.forEach { line ->
            CanvasCurve(
                line = line,
                xStepSize = chart.xStepSize,
                yStepSize = chart.yStepSize
            )
        }
    }
}

@Composable
fun MultiLineChart(
    lines: List<Line>
) {
    val chart = LineChart(
        lines = lines,
    )

    MultiLineChart(chart = chart)
}