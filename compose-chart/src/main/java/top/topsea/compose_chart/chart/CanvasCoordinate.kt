package top.topsea.compose_chart.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.rotateRad
import top.topsea.compose_chart.ChartConfig

@Composable
fun CanvasCoordinate(
    
) {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val canvas = drawContext.canvas
        val xEnd = size.width - ChartConfig.horPadding
        val yEnd = size.height - ChartConfig.verPadding
        val axisPaint = Paint().apply {
            color = Color.LightGray
            style = PaintingStyle.Stroke
            strokeWidth = 5f
        }

        drawAxisAndArrows(
            canvas = canvas,
            axisPaint = axisPaint,
            chartLayout = 300,
            xEnd = xEnd,
            yEnd = yEnd,
            withGrid = true,
            withArrow = true,
        )

    }
}

/**
 * @param xEnd: 宽减去padding
 * @param yEnd: 高减去padding
 */
private fun drawAxisAndArrows(
    canvas: Canvas,
    axisPaint: Paint,
    chartLayout: Int,
    xEnd: Float,
    yEnd: Float,
    withGrid: Boolean,
    withArrow: Boolean
) {
    val xAxis = Path()
    val yAxis = Path()

    when (chartLayout) {
        ChartConfig.LAYOUT_ALL_POS -> {
            //减去20是为了创造出交叉效果
            xAxis.moveTo((ChartConfig.horPadding - 20), yEnd)
            xAxis.lineTo(xEnd, yEnd)
            axisPaint.apply {
                strokeWidth = 5f
                style = PaintingStyle.Stroke
            }
            canvas.drawPath(xAxis, axisPaint)

            //加上20是为了创造出交叉效果
            yAxis.moveTo(ChartConfig.horPadding, ChartConfig.verPadding)
            yAxis.lineTo(ChartConfig.horPadding, yEnd + 20f)
            canvas.drawPath(yAxis, axisPaint)

            if (withGrid) {
                val xLines = (yEnd / ChartConfig.gridSize.value).toInt()
                val yLines = (xEnd / ChartConfig.gridSize.value).toInt()
                axisPaint.strokeWidth = 2f
                for (i in 1 until xLines - 1) {
                    xAxis.translate(Offset(0f, -ChartConfig.gridSize.value))
                    canvas.drawPath(xAxis, axisPaint)
                }

                for (i in 1 until yLines - 1) {
                    yAxis.translate(Offset(ChartConfig.gridSize.value, 0f))
                    canvas.drawPath(yAxis, axisPaint)
                }
            }

            if (withArrow) {
                val xArrows = Path()
                xArrows.moveTo(xEnd, yEnd - 15)
                xArrows.lineTo(xEnd, yEnd + 15)
                xArrows.lineTo(xEnd + 30, yEnd)
                xArrows.close()
                axisPaint.style = PaintingStyle.Fill
                canvas.drawPath(xArrows, axisPaint)

                val yArrows = Path()
                yArrows.moveTo(ChartConfig.horPadding - 15, ChartConfig.verPadding)
                yArrows.lineTo(ChartConfig.horPadding + 15, ChartConfig.verPadding)
                yArrows.lineTo(ChartConfig.horPadding, ChartConfig.verPadding - 30)
                yArrows.close()

                canvas.drawPath(yArrows, axisPaint)
            }
        }
        ChartConfig.LAYOUT_ALL -> {
            val xLines = (yEnd / ChartConfig.gridSize.value).toInt()
            val yLines = (xEnd / ChartConfig.gridSize.value).toInt()
            //x轴的y位置
            val xAxisPosition = yEnd - xLines / 2 * ChartConfig.gridSize.value
            //y轴的x位置
            val yAxisPosition = ChartConfig.horPadding + yLines / 2 * ChartConfig.gridSize.value
            //减去20是为了创造出交叉效果
            xAxis.moveTo((ChartConfig.horPadding - 20), xAxisPosition)
            xAxis.lineTo(xEnd, xAxisPosition)
            axisPaint.apply {
                strokeWidth = 5f
                style = PaintingStyle.Stroke
            }
            canvas.drawPath(xAxis, axisPaint)

            //加上20是为了创造出交叉效果
            yAxis.moveTo(yAxisPosition, ChartConfig.verPadding)
            yAxis.lineTo(yAxisPosition, yEnd + 20f)
            canvas.drawPath(yAxis, axisPaint)

            if (withGrid) {
                axisPaint.strokeWidth = 2f
                xAxis.translate(Offset(0f, (xLines / 2 * ChartConfig.gridSize.value)))
                for (i in 1 until xLines + 1) {
                    if (i == xLines / 2) {
                        continue
                    }
                    xAxis.translate(Offset(0f, -ChartConfig.gridSize.value))
                    canvas.drawPath(xAxis, axisPaint)
                }

                yAxis.translate(Offset(-(yLines / 2 * ChartConfig.gridSize.value), 0f))
                for (i in 1 until yLines) {
                    if (i == yLines / 2) {
                        continue
                    }
                    yAxis.translate(Offset(ChartConfig.gridSize.value, 0f))
                    canvas.drawPath(yAxis, axisPaint)
                }
            }

            if (withArrow) {
                val xArrows = Path()
                xArrows.moveTo(xEnd, xAxisPosition - 15)
                xArrows.lineTo(xEnd, xAxisPosition + 15)
                xArrows.lineTo(xEnd + 30, xAxisPosition)
                xArrows.close()
                axisPaint.style = PaintingStyle.Fill
                canvas.drawPath(xArrows, axisPaint)

                val yArrows = Path()
                yArrows.moveTo(yAxisPosition - 15, ChartConfig.verPadding)
                yArrows.lineTo(yAxisPosition + 15, ChartConfig.verPadding)
                yArrows.lineTo(yAxisPosition, ChartConfig.verPadding - 30)
                yArrows.close()

                canvas.drawPath(yArrows, axisPaint)
            }
        }
        ChartConfig.LAYOUT_X_POS -> {
            val xLines = (yEnd / ChartConfig.gridSize.value).toInt()
            val yLines = (xEnd / ChartConfig.gridSize.value).toInt()
            //y轴的x位置
            val yAxisPosition = ChartConfig.horPadding + yLines / 2 * ChartConfig.gridSize.value
            //减去20是为了创造出交叉效果
            xAxis.moveTo((ChartConfig.horPadding - 20), yEnd)
            xAxis.lineTo(xEnd, yEnd)
            axisPaint.apply {
                strokeWidth = 5f
                style = PaintingStyle.Stroke
            }
            canvas.drawPath(xAxis, axisPaint)

            //加上20是为了创造出交叉效果
            yAxis.moveTo(yAxisPosition, ChartConfig.verPadding)
            yAxis.lineTo(yAxisPosition, yEnd + 20f)
            canvas.drawPath(yAxis, axisPaint)

            if (withGrid) {
                axisPaint.strokeWidth = 2f
                for (i in 1 until xLines) {
                    xAxis.translate(Offset(0f, -ChartConfig.gridSize.value))
                    canvas.drawPath(xAxis, axisPaint)
                }

                yAxis.translate(Offset(-(yLines / 2 * ChartConfig.gridSize.value), 0f))
                for (i in 1 until yLines) {
                    if (i == yLines / 2) {
                        continue
                    }
                    yAxis.translate(Offset(ChartConfig.gridSize.value, 0f))
                    canvas.drawPath(yAxis, axisPaint)
                }
            }

            if (withArrow) {
                val xArrows = Path()
                xArrows.moveTo(xEnd, yEnd - 15)
                xArrows.lineTo(xEnd, yEnd + 15)
                xArrows.lineTo(xEnd + 30, yEnd)
                xArrows.close()
                axisPaint.style = PaintingStyle.Fill
                canvas.drawPath(xArrows, axisPaint)

                val yArrows = Path()
                yArrows.moveTo(yAxisPosition - 15, ChartConfig.verPadding)
                yArrows.lineTo(yAxisPosition + 15, ChartConfig.verPadding)
                yArrows.lineTo(yAxisPosition, ChartConfig.verPadding - 30)
                yArrows.close()

                canvas.drawPath(yArrows, axisPaint)
            }
        }
    }
}

private fun drawCoordinateText(
    canvas: Canvas,
    textPaint: NativePaint,
    chartLayout: Int,
    xEnd: Float,
    yEnd: Float,
    xUnit: String,
    yUnit: String,
) {
    when (chartLayout) {
        ChartConfig.LAYOUT_ALL_POS -> {
            val xLines = (yEnd / ChartConfig.gridSize.value).toInt()
            val yLines = (xEnd / ChartConfig.gridSize.value).toInt()
            val txtSize = textPaint.textSize
            val textCanvas = canvas.nativeCanvas
            //单位
            val xUt = if (xUnit.isNotEmpty()) {
                "x($xUnit)"
            } else {
                "x"
            }
            val yUt = if (yUnit.isNotEmpty()) {
                "y($yUnit)"
            } else {
                "y"
            }
            textCanvas.drawText(xUt,
                xEnd,
                yEnd + txtSize * 1.5f,
                textPaint
            )
            textCanvas.drawText(yUt,
                ChartConfig.horPadding - txtSize * 2f,
                ChartConfig.verPadding,
                textPaint
            )

            for (i in 0 until yLines - 1) {
                textCanvas.drawText(i.toString(),
                    ChartConfig.horPadding - txtSize / 2 + (i * ChartConfig.gridSize.value),
                    yEnd + txtSize,
                    textPaint
                )
            }
            for (j in xLines - 2 downTo 1) {
                textCanvas.drawText(j.toString(),
                    ChartConfig.horPadding - txtSize * 2,
                    yEnd + txtSize / 2 - (j * ChartConfig.gridSize.value),
                    textPaint
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
            val txtSize = textPaint.textSize
            val textCanvas = canvas.nativeCanvas
            //单位
            val xUt = if (xUnit.isNotEmpty()) {
                "x($xUnit)"
            } else {
                "x"
            }
            val yUt = if (yUnit.isNotEmpty()) {
                "y($yUnit)"
            } else {
                "y"
            }
            textCanvas.drawText(xUt,
                xEnd,
                xAxisPosition + txtSize * 1.5f,
                textPaint
            )
            textCanvas.drawText(yUt,
                yAxisPosition - txtSize * 2f,
                ChartConfig.verPadding,
                textPaint
            )

            var current = 0
            for (i in -yLines / 2 until yLines / 2) {
                textCanvas.drawText(i.toString(),
                    ChartConfig.horPadding - txtSize / 2 + (current * ChartConfig.gridSize.value),
                    xAxisPosition + txtSize,
                    textPaint
                )
                current++
            }
            current = 0
            for (j in -xLines / 2 until xLines / 2) {
                if (j == 0) {
                    current++
                    continue
                }
                textCanvas.drawText(j.toString(),
                    yAxisPosition - txtSize * 1.5f,
                    yEnd + txtSize / 2 - (current * ChartConfig.gridSize.value),
                    textPaint
                )
                current++
            }
        }
        ChartConfig.LAYOUT_X_POS -> {
            val xLines = (yEnd / ChartConfig.gridSize.value).toInt()
            val yLines = (xEnd / ChartConfig.gridSize.value).toInt()
            //y轴的x位置
            val yAxisPosition = ChartConfig.horPadding + yLines / 2 * ChartConfig.gridSize.value
            val txtSize = textPaint.textSize
            val textCanvas = canvas.nativeCanvas
            //单位
            val xUt = if (xUnit.isNotEmpty()) {
                "x($xUnit)"
            } else {
                "x"
            }
            val yUt = if (yUnit.isNotEmpty()) {
                "y($yUnit)"
            } else {
                "y"
            }
            textCanvas.drawText(xUt,
                xEnd,
                yEnd + txtSize * 1.5f,
                textPaint
            )
            textCanvas.drawText(yUt,
                yAxisPosition - txtSize * 2f,
                ChartConfig.verPadding,
                textPaint
            )

            var current = 0
            for (i in -yLines / 2 until yLines / 2) {
                textCanvas.drawText(i.toString(),
                    ChartConfig.horPadding - txtSize / 2 + (current * ChartConfig.gridSize.value),
                    yEnd + txtSize,
                    textPaint
                )
                current++
            }

            for (j in xLines - 1 downTo 1) {
                textCanvas.drawText(j.toString(),
                    yAxisPosition - txtSize * 1.5f,
                    yEnd + txtSize / 2 - (j * ChartConfig.gridSize.value),
                    textPaint
                )
                current++
            }
        }
    }
}
