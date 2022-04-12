package top.topsea.compose_chart.chart

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import top.topsea.compose_chart.ChartConfig
import top.topsea.compose_chart.LineConfig


fun drawDot(
    canvas: Canvas,
    values: List<Float>,
    height: Float,
    width: Float,
    chartLayout: Int,
    lineConfig: LineConfig,
    step: Float
) {

    val xEnd = width - ChartConfig.horPadding
    val yEnd = height - ChartConfig.verPadding

    val listDot = handleValues(
        values = values,
        xEnd = xEnd,
        yEnd = yEnd,
        chartLayout = chartLayout,
        step = step
    )

    val line = Path()
    listDot.forEach { offset ->
        if (offset == listDot.first()) {
            line.moveTo(offset.x, offset.y)
        } else {
            line.lineTo(offset.x, offset.y)
        }
        if (lineConfig.withDot) {
            val dotPaint = lineConfig.axisPaint.apply {
                style = PaintingStyle.Fill
            }
            canvas.drawCircle(offset, 8f, dotPaint)
        }
    }
    val linePaint = lineConfig.axisPaint.apply {
        style = PaintingStyle.Stroke
    }
    canvas.drawPath(line, linePaint)

}

private fun handleValues(
    values: List<Float>,
    xEnd: Float,
    yEnd: Float,
    chartLayout: Int,
    step: Float
) : List<Offset>{
    if (values.isEmpty()) {
        throw RuntimeException("Where were your values?")
    }
    val listDot = mutableListOf<Offset>()
    when (chartLayout) {
        ChartConfig.LAYOUT_ALL_POS -> {
            values.forEachIndexed { index, value ->
                listDot.add(
                    Offset(
                        index * ChartConfig.gridSize.value * step + ChartConfig.horPadding,
                        yEnd - value * ChartConfig.gridSize.value
                    )
                )
            }
        }
        ChartConfig.LAYOUT_ALL -> {
            val xLines = (yEnd / ChartConfig.gridSize.value).toInt()
            val yLines = (xEnd / ChartConfig.gridSize.value).toInt()
            //x轴的y位置
            val xAxisPosition = yEnd - xLines / 2 * ChartConfig.gridSize.value
            //y轴的x位置
            val yAxisPosition = ChartConfig.horPadding + yLines / 2 * ChartConfig.gridSize.value
            values.forEachIndexed { index, value ->
                listDot.add(
                    Offset(
                        index * ChartConfig.gridSize.value * step + yAxisPosition,
                        xAxisPosition - value * ChartConfig.gridSize.value * step
                    )
                )
                println("gaohai:::${listDot.last()}")
            }
        }
        ChartConfig.LAYOUT_X_POS -> {
            val yLines = (xEnd / ChartConfig.gridSize.value).toInt()
            //y轴的x位置
            val yAxisPosition = ChartConfig.horPadding + yLines / 2 * ChartConfig.gridSize.value
            values.forEachIndexed { index, value ->
                listDot.add(
                    Offset(
                        index * ChartConfig.gridSize.value * step + yAxisPosition,
                        yEnd - value * ChartConfig.gridSize.value * step
                    )
                )
                println("gaohai:::${listDot.last()}")
            }
        }
    }
    return listDot
}
