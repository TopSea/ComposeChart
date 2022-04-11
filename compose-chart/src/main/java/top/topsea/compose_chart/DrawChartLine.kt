package top.topsea.compose_chart

import android.graphics.PointF
import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import kotlin.math.abs


fun drawLine(
    canvas: Canvas,
    values: List<Float>,
    height: Float,
    width: Float,
    chartLayout: Int,
    lineConfig: LineConfig,
    dotClicked: MutableState<Int>?,
) {

    val xEnd = width - ChartConfig.horPadding
    val yEnd = height - ChartConfig.verPadding

    val listDot = handleValues(
        values = values,
        xEnd = xEnd,
        yEnd = yEnd,
        chartLayout = chartLayout
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

    //保证信息显示在最上层
    if (lineConfig.withInfo && dotClicked != null && dotClicked.value != Int.MAX_VALUE) {
        drawDotInfo(
            canvas = canvas,
            listDot = listDot,
            lineConfig = lineConfig,
            width = width,
            witchOne = dotClicked.value
        )
    }
}

private fun handleValues(
    values: List<Float>,
    xEnd: Float,
    yEnd: Float,
    chartLayout: Int,
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
                        index * ChartConfig.gridSize.value + ChartConfig.horPadding,
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
                        index * ChartConfig.gridSize.value + yAxisPosition,
                        xAxisPosition - value * ChartConfig.gridSize.value
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
                        index * ChartConfig.gridSize.value + yAxisPosition,
                        yEnd - value * ChartConfig.gridSize.value
                    )
                )
                println("gaohai:::${listDot.last()}")
            }
        }
    }
    return listDot
}


private fun drawDotInfo(
    canvas: Canvas,
    listDot: List<Offset>,
    lineConfig: LineConfig,
    width: Float,
    witchOne: Int,
    info: String = "Let's write info about this dot."
) {
    val textPaint = lineConfig.textPaint!!
    var infoStart = listDot[witchOne].x
    var infoEnd = listDot[witchOne].x + lineConfig.infoWidth!! * 2
    var infoTop = listDot[witchOne].y - lineConfig.infoHeight!!
    var infoBottom = listDot[witchOne].y

    if (infoEnd > width) {
        infoStart = listDot[witchOne].x - lineConfig.infoWidth!! * 2
        infoEnd = listDot[witchOne].x
    }
    if (infoTop < 0) {
        infoTop = listDot[witchOne].y
        infoBottom = listDot[witchOne].y + lineConfig.infoHeight!!
    }
    val center = (infoStart + infoEnd) / 2

    val infoRectPaint = Paint().apply {
        style = PaintingStyle.Fill
        color = Color.LightGray
        alpha = 0.5f
        strokeWidth = 3f
    }

    canvas.drawRoundRect(
        left = infoStart,
        top = infoTop,
        right = infoEnd,
        bottom = infoBottom,
        radiusX = 15f,
        radiusY = 15f,
        infoRectPaint
    )

    textPaint.textSize = 50f
    canvas.nativeCanvas.drawText("Title",
        center - textPaint.textSize,
        infoTop + 60f,
        textPaint
    )
    textPaint.textSize = 30f

    //内容过长需要换行
    val limit = lineConfig.infoWidth!! - 40f
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

fun drawCurve(
    canvas: Canvas,
    values: List<Float>,
    height: Float,
    width: Float,
    chartLayout: Int,
    lineConfig: LineConfig,
    dotClicked: MutableState<Int>?,
) {

    val xEnd = width - ChartConfig.horPadding
    val yEnd = height - ChartConfig.verPadding

    val listDot = handleValues(
        values = values,
        xEnd = xEnd,
        yEnd = yEnd,
        chartLayout = chartLayout
    )

    val curvePath = Path()
    curvePath.moveTo(listDot.first().x, listDot.first().y)

    for (index in 0 until listDot.size - 1) {
        val xMoveDistance = 10
        val yMoveDistance = 20

        if (lineConfig.withDot) {
            val dotPaint = lineConfig.axisPaint.apply {
                style = PaintingStyle.Fill
            }
            canvas.drawCircle(listDot[index], 8f, dotPaint)
        }

        if (listDot[index].y == listDot[index + 1].y) {
            curvePath.lineTo(listDot[index + 1].x, listDot[index + 1].y)
        } else {
            val centerX = (listDot[index].x + listDot[index + 1].x) / 2
            val centerY = (listDot[index].y + listDot[index + 1].y) / 2
            val smoothX = abs(listDot[index].y - listDot[index + 1].y) * 0.1f
            val smoothY = abs(listDot[index].y - listDot[index + 1].y) * 0.2f

            if (listDot[index].y < listDot[index + 1].y) {
                val ctlX0 = (listDot[index].x + centerX) / 2 + smoothX
                val ctlY0 = (listDot[index].y + centerY) / 2 - smoothY
                val ctlX1 = (centerX + listDot[index + 1].x) / 2 - smoothX
                val ctlY1 = (centerY + listDot[index + 1].y) / 2 + smoothY
                curvePath.cubicTo(
                    ctlX0 + xMoveDistance,
                    ctlY0 - yMoveDistance,
                    ctlX1 - xMoveDistance,
                    ctlY1 + yMoveDistance,
                    listDot[index + 1].x,
                    listDot[index + 1].y
                )
            } else {
                val ctlX0 = (listDot[index].x + centerX) / 2 + smoothX
                val ctlY0 = (listDot[index].y + centerY) / 2 + smoothY
                val ctlX1 = (centerX + listDot[index + 1].x) / 2 - smoothX
                val ctlY1 = (centerY + listDot[index + 1].y) / 2 - smoothY
                curvePath.cubicTo(
                    ctlX0 + xMoveDistance,
                    ctlY0 + yMoveDistance,
                    ctlX1 - xMoveDistance,
                    ctlY1 - yMoveDistance,
                    listDot[index + 1].x,
                    listDot[index + 1].y
                )
            }
        }
    }

    if (lineConfig.withDot) {
        val dotPaint = lineConfig.axisPaint.apply {
            style = PaintingStyle.Fill
        }
        canvas.drawCircle(listDot.last(), 8f, dotPaint)
    }

    val linePaint = lineConfig.axisPaint.apply {
        style = PaintingStyle.Stroke
    }
    canvas.drawPath(curvePath, linePaint)

    //保证信息显示在最上层
    if (lineConfig.withInfo && dotClicked != null && dotClicked.value != Int.MAX_VALUE) {
        drawDotInfo(
            canvas = canvas,
            listDot = listDot,
            lineConfig = lineConfig,
            width = width,
            witchOne = dotClicked.value
        )
    }
}
