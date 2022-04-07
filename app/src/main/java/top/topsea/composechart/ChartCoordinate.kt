package top.topsea.composechart

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import kotlin.math.floor

fun drawChartCoordinate(
    canvas: Canvas,
    height: Float,
    width: Float,
    chartLayout: Int,
    coordinateConfig: CoordinateConfig,
    xUnit: String = "元",
    yUnit: String = "斤",
) {

    val xEnd = width - ChartConfig.horPadding
    val yEnd = height - ChartConfig.verPadding

    //画网格
    drawAxisAndArrows(
        canvas = canvas,
        axisPaint = coordinateConfig.axisPaint,
        xEnd = xEnd,
        yEnd = yEnd,
        withGrid = coordinateConfig.withGrid,
        withArrow = coordinateConfig.withArrow
    )

    //画文字
    if (coordinateConfig.withText) {
        drawCoordinateText(
            canvas = canvas,
            textPaint = coordinateConfig.textPaint,
            xEnd = xEnd,
            yEnd = yEnd,
            xUnit = xUnit,
            yUnit = yUnit,
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
    xEnd: Float,
    yEnd: Float,
    withGrid: Boolean,
    withArrow: Boolean
) {
    val xAxis = Path()
    //减去20是为了创造出交叉效果
    xAxis.moveTo((ChartConfig.horPadding - 20), yEnd)
    xAxis.lineTo(xEnd, yEnd)
    axisPaint.apply {
        strokeWidth = 5f
        style = PaintingStyle.Stroke
    }
    canvas.drawPath(xAxis, axisPaint)

    val yAxis = Path()
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

private fun drawCoordinateText(
    canvas: Canvas,
    textPaint: NativePaint,
    xEnd: Float,
    yEnd: Float,
    xUnit: String,
    yUnit: String,
) {
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

fun drawLine(
    canvas: Canvas,
    values: List<Float>,
    height: Float,
    width: Float,
    lineConfig: LineConfig,
    dotClicked: MutableState<Int>?,
) {
    val listDot = mutableListOf<Offset>()
    val bottom = height - ChartConfig.verPadding
    values.forEachIndexed { index, value ->
        listDot.add(
            Offset(
            index * ChartConfig.gridSize.value + ChartConfig.horPadding,
            bottom - value * ChartConfig.gridSize.value
            )
        )
    }

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

    //保证信息显示在最上层
    if (lineConfig.withInfo && dotClicked != null && dotClicked.value != Int.MAX_VALUE) {
        drawDotInfo(
            canvas = canvas,
            listDot = listDot,
            textPaint = lineConfig.textPaint!!,
            width = width,
            witchOne = dotClicked.value
        )
    }
}

fun drawDotInfo(
    canvas: Canvas,
    listDot: List<Offset>,
    textPaint: NativePaint,
    width: Float,
    witchOne: Int,
    info: String = "Let's write info about this dot."
) {
    var infoStart = listDot[witchOne].x
    var infoEnd = listDot[witchOne].x + ChartConfig.infoWidth * 2
    var infoTop = listDot[witchOne].y - ChartConfig.infoHeight
    var infoBottom = listDot[witchOne].y

    if (infoEnd > width) {
        infoStart = listDot[witchOne].x - ChartConfig.infoWidth * 2
        infoEnd = listDot[witchOne].x
    }
    if (infoTop < 0) {
        infoTop = listDot[witchOne].y
        infoBottom = listDot[witchOne].y + ChartConfig.infoHeight
    }
    val center = (infoStart + infoEnd) / 2

    val infoRectPaint = Paint().apply {
        style = PaintingStyle.Fill
        color = Color.LightGray
        alpha = 0.5f
        strokeWidth = 3f
    }

    canvas.drawRect(infoStart, infoTop, infoEnd, infoBottom, infoRectPaint)

    textPaint.textSize = 50f
    canvas.nativeCanvas.drawText("Title",
        center - textPaint.textSize,
        infoTop + 60f,
        textPaint
    )
    textPaint.textSize = 30f

    //内容过长需要换行
    val limit = ChartConfig.infoWidth - 40f
    val limitChars = (limit / 6f).toInt()
    if (info.length > limitChars) {
        val subStr1 = info.substring(0, limitChars)
        canvas.nativeCanvas.drawText(subStr1,
            infoStart + 20f,
            infoTop + 100f,
            textPaint
        )

        val subStr2 = info.substring(limitChars)
        canvas.nativeCanvas.drawText(subStr2,
            infoStart + 20f,
            infoTop + 140f,
            textPaint
        )
    } else {
        canvas.nativeCanvas.drawText(info,
            infoStart + 20f,
            infoTop + 100f,
            textPaint
        )
    }
    canvas.drawCircle(listDot[witchOne], 16f, Paint().apply {
        style = PaintingStyle.Fill
        color = Color.Red
        strokeWidth = 3f
    })
}
