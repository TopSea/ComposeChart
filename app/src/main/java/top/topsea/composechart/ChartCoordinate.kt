package top.topsea.composechart

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import kotlin.math.floor

fun drawChartCoordinate(
    canvas: Canvas,
    height: Float,
    width: Float,
    withGrid: Boolean = true,
    withArrow: Boolean = true,
    withText: Boolean = true,
    xUnit: String = "元",
    yUnit: String = "斤",
) {
    val xLines = floor(height / ChartConfig.gridSize.value).toInt()
    val yLines = floor(width / ChartConfig.gridSize.value).toInt()
    val xAxis = Path()
    val bottom = height - ChartConfig.verPadding
    //减去20是为了创造出交叉效果
    xAxis.moveTo((ChartConfig.horPadding - 20), bottom)
    xAxis.lineTo(width - ChartConfig.horPadding, bottom)
    val xAxisPaint = Paint().apply {
        strokeWidth = 5f
        color = Color.LightGray
        style = PaintingStyle.Stroke
    }
    canvas.drawPath(xAxis, xAxisPaint)

    val yAxis = Path()
    //加上20是为了创造出交叉效果
    yAxis.moveTo(ChartConfig.horPadding, ChartConfig.verPadding)
    yAxis.lineTo(ChartConfig.horPadding, bottom + 20f)
    val yAxisPaint = Paint().apply {
        strokeWidth = 5f
        color = Color.LightGray
        style = PaintingStyle.Stroke
    }
    canvas.drawPath(yAxis, yAxisPaint)

    //画网格
    if (withGrid) {
        xAxisPaint.strokeWidth = 2f
        for (i in 1 until xLines - 1) {
            xAxis.translate(Offset(0f, -ChartConfig.gridSize.value))
            canvas.drawPath(xAxis, xAxisPaint)
        }
        yAxisPaint.strokeWidth = 2f
        for (i in 1 until yLines - 1) {
            yAxis.translate(Offset(ChartConfig.gridSize.value, 0f))
            canvas.drawPath(yAxis, yAxisPaint)
        }
    }
    //画箭头
    if (withArrow) {
        val xArrows = Path()
        xArrows.moveTo(width - ChartConfig.horPadding, bottom - 15)
        xArrows.lineTo(width - ChartConfig.horPadding, bottom + 15)
        xArrows.lineTo(width - ChartConfig.horPadding + 30, bottom)
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

    //画文字
    if (withText) {
        val txtSize = 24f
        val textPaint = NativePaint().apply {
            color = android.graphics.Color.BLACK
            style = android.graphics.Paint.Style.FILL
            strokeWidth = 1f
            textSize = txtSize
        }
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
            width - ChartConfig.horPadding,
            bottom + txtSize * 1.5f,
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
                bottom + txtSize,
                textPaint
            )
        }
        for (j in xLines - 2 downTo 1) {
            textCanvas.drawText(j.toString(),
                ChartConfig.horPadding - txtSize * 2,
                bottom + txtSize / 2 - (j * ChartConfig.gridSize.value),
                textPaint
            )
        }
    }
}

fun drawLine(
    canvas: Canvas,
    values: List<Float>,
    height: Float,
    width: Float,
    dotClicked: MutableState<Int>?,
    withDot: Boolean = true
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
        if (withDot) {
            val dotPaint = Paint().apply {
                style = PaintingStyle.Fill
                color = Color.Red
                strokeWidth = 3f
            }
            canvas.drawCircle(offset, 8f, dotPaint)
        }
    }
    val linePaint = Paint().apply {
        style = PaintingStyle.Stroke
        color = Color.Red
        strokeWidth = 2f
    }
    canvas.drawPath(line, linePaint)

    //保证信息显示在最上层
    if (dotClicked != null && dotClicked.value != Int.MAX_VALUE) {
        drawDotInfo(
            canvas = canvas,
            listDot = listDot,
            width = width,
            witchOne = dotClicked.value
        )
    }
}

fun drawDotInfo(
    canvas: Canvas,
    listDot: List<Offset>,
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

    val textPaint = NativePaint().apply {
        color = android.graphics.Color.BLACK
        style = android.graphics.Paint.Style.FILL
        strokeWidth = 1f
        textSize = 40f
    }

    canvas.drawRect(infoStart, infoTop, infoEnd, infoBottom, infoRectPaint)

    canvas.nativeCanvas.drawText("Title",
        center - textPaint.textSize,
        infoTop + 50f,
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
