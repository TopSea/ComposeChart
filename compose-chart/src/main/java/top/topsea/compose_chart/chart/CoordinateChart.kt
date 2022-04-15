package top.topsea.compose_chart.chart

import android.util.Range
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import top.topsea.compose_chart.ChartConfig

open class CoordinateChart(
    val xRange: Range<Float> = Range(0f, 10f),
    var xStep: Float = 1.0f,
    val yRange: Range<Float> = Range(0f, 10f),
    var yStep: Float = 1.0f,
): Chart {
    open var withGrid: Boolean = true
    open var withArrow: Boolean = true
    open var withText: Boolean = true
    open var axisUnit: Array<String>? = null

    var xEnd = 0f
    var yEnd = 0f
    var xStepSize = 100f
    var yStepSize = 100f

    val padding = 80f
    var model = MODEL_ALL
    companion object {
        val MODEL_ALL_POS = 1000
        val MODEL_X_POS = 1200
        val MODEL_ALL = 1300
    }

    init {
        model = if (xRange.lower >= 0 && yRange.lower >= 0) {
            MODEL_ALL_POS
        } else if (xRange.lower < 0 && yRange.lower >= 0) {
            MODEL_X_POS
        } else {
            MODEL_ALL
        }
    }

    private fun handleCoordinate(
        gridSize: Float,
        height: Float,
        width: Float,
    ) {
        xEnd = width - padding
        yEnd = height - padding
        xStepSize = xStep * gridSize
        yStepSize = yStep * gridSize
    }

    fun drawCoordinate(
        canvas: Canvas,
        axisPaint: Paint,
        textPaint: NativePaint,
        gridSize: Float,
        height: Float,
        width: Float,
    ) {
        handleCoordinate(gridSize, height, width)

        drawAxisAndArrows(canvas, axisPaint)

        drawCoordinateText(canvas, textPaint)
    }

    private fun drawAxisAndArrows(
        canvas: Canvas,
        axisPaint: Paint,
    ) {
        val xAxis = Path()
        val yAxis = Path()

        //间隔太小不显示文字
        if (xStepSize < 70f || yStepSize < 70f) {
            withText = false
        }

        when (model) {
            MODEL_ALL_POS -> {
                //减去20是为了创造出交叉效果
                xAxis.moveTo((padding - 20), yEnd)
                xAxis.lineTo(xEnd, yEnd)
                axisPaint.apply {
                    strokeWidth = 5f
                    style = PaintingStyle.Stroke
                }
                canvas.drawPath(xAxis, axisPaint)

                //加上20是为了创造出交叉效果
                yAxis.moveTo(padding, padding)
                yAxis.lineTo(padding, yEnd + 20f)
                canvas.drawPath(yAxis, axisPaint)

                if (withGrid) {
                    val xLines = (yEnd / xStepSize).toInt()
                    val yLines = (xEnd / yStepSize).toInt()
                    axisPaint.strokeWidth = 2f
                    for (i in 1 until xLines - 1) {
                        xAxis.translate(Offset(0f, -xStepSize))
                        canvas.drawPath(xAxis, axisPaint)
                    }

                    for (i in 1 until yLines - 1) {
                        yAxis.translate(Offset(yStepSize, 0f))
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
                    yArrows.moveTo(padding - 15, padding)
                    yArrows.lineTo(padding + 15, padding)
                    yArrows.lineTo(padding, padding - 30)
                    yArrows.close()

                    canvas.drawPath(yArrows, axisPaint)
                }
            }
            MODEL_ALL -> {
                val xLines = (yEnd / xStepSize).toInt()
                val yLines = (xEnd / yStepSize).toInt()
                //x轴的y位置
                val xAxisPosition = yEnd - xLines / 2 * xStepSize
                //y轴的x位置
                val yAxisPosition = padding + yLines / 2 * yStepSize
                //减去20是为了创造出交叉效果
                xAxis.moveTo((padding - 20), xAxisPosition)
                xAxis.lineTo(xEnd, xAxisPosition)
                axisPaint.apply {
                    strokeWidth = 5f
                    style = PaintingStyle.Stroke
                }
                canvas.drawPath(xAxis, axisPaint)

                //加上20是为了创造出交叉效果
                yAxis.moveTo(yAxisPosition, padding)
                yAxis.lineTo(yAxisPosition, yEnd + 20f)
                canvas.drawPath(yAxis, axisPaint)

                if (withGrid) {
                    axisPaint.strokeWidth = 2f
                    xAxis.translate(Offset(0f, (xLines / 2 * xStepSize)))
                    for (i in 1 until xLines + 1) {
                        if (i == xLines / 2) {
                            continue
                        }
                        xAxis.translate(Offset(0f, -xStepSize))
                        canvas.drawPath(xAxis, axisPaint)
                    }

                    yAxis.translate(Offset(-(yLines / 2 * yStepSize), 0f))
                    for (i in 1 until yLines) {
                        if (i == yLines / 2) {
                            continue
                        }
                        yAxis.translate(Offset(yStepSize, 0f))
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
                    yArrows.moveTo(yAxisPosition - 15, padding)
                    yArrows.lineTo(yAxisPosition + 15, padding)
                    yArrows.lineTo(yAxisPosition, padding - 30)
                    yArrows.close()

                    canvas.drawPath(yArrows, axisPaint)
                }
            }
            MODEL_X_POS -> {
                val xLines = (yEnd / xStepSize).toInt()
                val yLines = (xEnd / yStepSize).toInt()
                //y轴的x位置
                val yAxisPosition = padding + yLines / 2 * yStepSize
                //减去20是为了创造出交叉效果
                xAxis.moveTo((padding - 20), yEnd)
                xAxis.lineTo(xEnd, yEnd)
                axisPaint.apply {
                    strokeWidth = 5f
                    style = PaintingStyle.Stroke
                }
                canvas.drawPath(xAxis, axisPaint)

                //加上20是为了创造出交叉效果
                yAxis.moveTo(yAxisPosition, padding)
                yAxis.lineTo(yAxisPosition, yEnd + 20f)
                canvas.drawPath(yAxis, axisPaint)

                if (withGrid) {
                    axisPaint.strokeWidth = 2f
                    for (i in 1 until xLines) {
                        xAxis.translate(Offset(0f, -xStepSize))
                        canvas.drawPath(xAxis, axisPaint)
                    }

                    yAxis.translate(Offset(-(yLines / 2 * yStepSize), 0f))
                    for (i in 1 until yLines) {
                        if (i == yLines / 2) {
                            continue
                        }
                        yAxis.translate(Offset(yStepSize, 0f))
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
                    yArrows.moveTo(yAxisPosition - 15, padding)
                    yArrows.lineTo(yAxisPosition + 15, padding)
                    yArrows.lineTo(yAxisPosition, padding - 30)
                    yArrows.close()

                    canvas.drawPath(yArrows, axisPaint)
                }
            }
        }
    }

    private fun drawCoordinateText(
        canvas: Canvas,
        textPaint: NativePaint,
    ) {
        val xUt: String
        val yUt: String
        //单位
        if (axisUnit != null && axisUnit!!.isNotEmpty()) {
            xUt = "x(${axisUnit!![0]})"
            yUt = "y(${axisUnit!![1]})"
        } else {
            xUt = "x"
            yUt = "y"
        }
        val xLines = (yEnd / xStepSize).toInt()
        val yLines = (xEnd / yStepSize).toInt()
        val txtSize = textPaint.textSize
        val textCanvas = canvas.nativeCanvas
        when (model) {
            MODEL_ALL_POS -> {
                textCanvas.drawText(xUt,
                    xEnd - 20f,
                    yEnd + txtSize * 1.5f,
                    textPaint
                )
                textCanvas.drawText(yUt,
                    padding - txtSize * 2f,
                    padding,
                    textPaint
                )

                for (i in 0 until yLines - 1) {
                    textCanvas.drawText(i.toString(),
                        padding - txtSize / 2 + (i * yStepSize),
                        yEnd + txtSize * 1.5f,
                        textPaint
                    )
                }
                for (j in xLines - 2 downTo 1) {
                    textCanvas.drawText(j.toString(),
                        padding - txtSize * 2,
                        yEnd + txtSize / 2 - (j * xStepSize),
                        textPaint
                    )
                }
            }
            MODEL_ALL -> {
                //x轴的y位置
                val xAxisPosition = yEnd - xLines / 2 * yStepSize
                //y轴的x位置
                val yAxisPosition = padding + yLines / 2 * xStepSize
                textCanvas.drawText(xUt,
                    xEnd - 20f,
                    xAxisPosition + txtSize * 1.5f,
                    textPaint
                )
                textCanvas.drawText(yUt,
                    yAxisPosition - txtSize * 2f,
                    padding,
                    textPaint
                )

                var current = 0
                for (i in -yLines / 2 until yLines / 2) {
                    textCanvas.drawText(i.toString(),
                        padding - txtSize / 2 + (current * yStepSize),
                        xAxisPosition + txtSize * 1.5f,
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
                        yEnd + txtSize / 2 - (current * xStepSize),
                        textPaint
                    )
                    current++
                }
            }
            MODEL_X_POS -> {
                //y轴的x位置
                val yAxisPosition = padding + yLines / 2 * yStepSize
                textCanvas.drawText(xUt,
                    xEnd - 20f,
                    yEnd + txtSize * 1.5f,
                    textPaint
                )
                textCanvas.drawText(yUt,
                    yAxisPosition - txtSize * 2f,
                    padding,
                    textPaint
                )

                var current = 1
                for (i in -yLines / 2 + 1 until yLines / 2 - 1) {
                    textCanvas.drawText(i.toString(),
                        padding - txtSize / 2 + (current * yStepSize),
                        yEnd + txtSize * 1.5f,
                        textPaint
                    )
                    current++
                }

                for (j in xLines - 1 downTo 1) {
                    textCanvas.drawText(j.toString(),
                        yAxisPosition - txtSize * 1.5f,
                        yEnd + txtSize / 2 - (j * xStepSize),
                        textPaint
                    )
                }
            }
        }
    }

    override fun drawChart() {
//        TODO("Not yet implemented")
    }

    override fun export() {
//        TODO("Not yet implemented")
    }
}