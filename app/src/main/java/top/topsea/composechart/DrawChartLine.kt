package top.topsea.composechart

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*


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
            textPaint = lineConfig.textPaint!!,
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
