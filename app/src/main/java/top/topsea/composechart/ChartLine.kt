package top.topsea.composechart

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*


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
