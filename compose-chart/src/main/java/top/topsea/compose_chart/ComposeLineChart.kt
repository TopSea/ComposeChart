package top.topsea.compose_chart

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.topsea.compose_chart.chart.CanvasCoordinate
import top.topsea.compose_chart.chart.CanvasCurve
import top.topsea.compose_chart.chart.Line
import top.topsea.compose_chart.chart.LineChart
import androidx.compose.ui.platform.LocalDensity

@Composable
fun MultiLineChart(
    chart: LineChart
) {
    var scrollState = rememberScrollState()
    var state = rememberTransformableState { zoomChange, _, _ ->
        if (chart.gridSize.value.toInt() in 80..200) {
            ChartConfig.gridSize.value = ChartConfig.gridSize.value * zoomChange
        }
        if (ChartConfig.gridSize.value < 50f) {
            ChartConfig.gridSize.value = 50f
        } else if (ChartConfig.gridSize.value > 200f) {
            ChartConfig.gridSize.value = 200f
        }
    }
    val widthDp = LocalDensity.current.run { chart.width.toDp() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .setChartScrollable((chart.scrollable && widthDp > 0.dp), scrollState)
    ) {
        CanvasCoordinate(modifier = Modifier.requiredWidth(widthDp), chart = chart)

        chart.lines.forEach { line ->
            CanvasCurve(
                modifier = Modifier.requiredWidth(widthDp),
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

private fun Modifier.setChartScrollable(
    scrollable: Boolean,
    scrollState: ScrollState?
) = this.then(
    if (scrollable) {
        horizontalScroll(scrollState!!)
    } else {
        Modifier
    }
)