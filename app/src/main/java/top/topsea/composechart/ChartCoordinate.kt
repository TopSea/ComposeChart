package top.topsea.composechart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import kotlin.math.floor


fun drawChartCoordinate(
    canvas: Canvas,
    height: Float,
    width: Float,
    withGrid: Boolean = true,
    withArrow: Boolean = true,
) {
    val xAxis = Path()
    //减去20是为了创造出交叉效果
    xAxis.moveTo((ChartConfig.horPadding - 20), height - ChartConfig.verPadding)
    xAxis.lineTo(width - ChartConfig.horPadding, height - ChartConfig.verPadding)
    val xAxisPaint = Paint().apply {
        strokeWidth = 5f
        color = Color.LightGray
        style = PaintingStyle.Stroke
    }
    canvas.drawPath(xAxis, xAxisPaint)

    val yAxis = Path()
    //加上20是为了创造出交叉效果
    yAxis.moveTo(ChartConfig.horPadding, ChartConfig.verPadding)
    yAxis.lineTo(ChartConfig.horPadding, height - ChartConfig.verPadding + 20)
    val yAxisPaint = Paint().apply {
        strokeWidth = 5f
        color = Color.LightGray
        style = PaintingStyle.Stroke
    }
    canvas.drawPath(yAxis, yAxisPaint)

    //画网格
    if (withGrid) {
        val xLines = floor(height / 100).toInt()
        xAxisPaint.strokeWidth = 2f
        for (i in 1 until xLines - 1) {
            xAxis.translate(Offset(0f, -100f))
            canvas.drawPath(xAxis, xAxisPaint)
        }
        val yLines = floor(width / 100).toInt()
        yAxisPaint.strokeWidth = 2f
        for (i in 1 until yLines - 1) {
            yAxis.translate(Offset(100f, 0f))
            canvas.drawPath(yAxis, yAxisPaint)
        }
    }
    //画箭头
    if (withArrow) {
        val xArrows = Path()
        xArrows.moveTo(width - ChartConfig.horPadding, height - ChartConfig.verPadding - 15)
        xArrows.lineTo(width - ChartConfig.horPadding, height - ChartConfig.verPadding + 15)
        xArrows.lineTo(width - ChartConfig.horPadding + 30, height - ChartConfig.verPadding)
        xArrows.close()
        xAxisPaint.style = PaintingStyle.Fill
        canvas.drawPath(xArrows, xAxisPaint)

        val yArrows = Path()
        yArrows.moveTo(ChartConfig.horPadding - 15, ChartConfig.verPadding)
        yArrows.lineTo(ChartConfig.horPadding + 15, ChartConfig.verPadding)
        yArrows.lineTo(ChartConfig.horPadding, ChartConfig.verPadding - 30)
        yArrows.close()
        yAxisPaint.style = PaintingStyle.Fill
        canvas.drawPath(yArrows, yAxisPaint)
    }
}

