package top.topsea.compose_chart.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.rotateRad
import androidx.compose.ui.unit.dp
import top.topsea.compose_chart.ChartConfig

@Composable
fun CanvasCoordinate(
    modifier: Modifier,
    chart: LineChart
) {
    Canvas(
        modifier = modifier
            .fillMaxHeight()
    ) {
        println("gaohai:::size.height${size.height} size.width${size.width}")
        val canvas = drawContext.canvas
        val xEnd = size.width - CoordinateChart.padding
        if (chart.withChartInfo) {
            canvas.drawRect(
                left = size.width - 360f,
                top = 0f,
                right = size.width,
                bottom = 80f * chart.lines.size,
                paint = Paint().apply {
                    style = PaintingStyle.Fill
                    color = Color.LightGray
                }
            )
            canvas.drawRect(
                left = size.width - 360f,
                top = 0f,
                right = size.width,
                bottom = 80f * chart.lines.size,
                paint = Paint().apply {
                    strokeWidth = 2f
                    style = PaintingStyle.Stroke
                    color = Color.Black
                }
            )

            chart.lines.forEachIndexed{ index, line ->
                chart.drawChartInfo(
                    canvas = canvas,
                    name = line.name,
                    xEnd = xEnd + ChartConfig.horPadding,
                    textTop = 80f * index + 40f,
                    linePaint = line.linePaint
                )
            }
        }
        println("gaohai:::size.height${size.height} size.width${size.width}")
        chart.drawCoordinate(
            canvas = canvas,
            axisPaint = Paint().apply {
                style = PaintingStyle.Stroke
                color = Color.LightGray
                strokeWidth = 2f
            },
            textPaint = NativePaint().apply {
                color = android.graphics.Color.BLACK
                style = android.graphics.Paint.Style.FILL
                strokeWidth = 1f
                textSize = 24f
            },
            gridSize = chart.gridSize.value,
            height = size.height,
            width = size.width,
        )
    }
}
