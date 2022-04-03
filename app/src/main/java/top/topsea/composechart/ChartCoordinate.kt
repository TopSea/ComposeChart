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
    withText: Boolean = true,
    xUnit: String = "元",
    yUnit: String = "斤",
) {
    val xLines = floor(height / ChartConfig.gridSize).toInt()
    val yLines = floor(width / ChartConfig.gridSize).toInt()
    val xAxis = Path()
    val bottom = (xLines - 1) * ChartConfig.gridSize + ChartConfig.verPadding
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
    yAxis.lineTo(ChartConfig.horPadding, bottom + 20)
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
            xAxis.translate(Offset(0f, -ChartConfig.gridSize))
            canvas.drawPath(xAxis, xAxisPaint)
        }
        yAxisPaint.strokeWidth = 2f
        for (i in 1 until yLines - 1) {
            yAxis.translate(Offset(ChartConfig.gridSize, 0f))
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
                ChartConfig.horPadding - txtSize / 2 + (i * ChartConfig.gridSize),
                bottom + txtSize,
                textPaint
            )
        }
        for (j in xLines - 2 downTo 1) {
            textCanvas.drawText(j.toString(),
                ChartConfig.horPadding - txtSize * 2,
                bottom + txtSize / 2 - (j * ChartConfig.gridSize),
                textPaint
            )
        }
    }
}

fun drawLine(
    canvas: Canvas,
    values: List<Float>,
    height: Float,
    dotClicked: Int,
    withDot: Boolean = true
) {
    val listDot = mutableListOf<Offset>()
    val xLines = floor(height / ChartConfig.gridSize).toInt()
    val bottom = (xLines - 1) * ChartConfig.gridSize + ChartConfig.verPadding
    values.forEachIndexed { index, value ->
        listDot.add(
            Offset(
            index * ChartConfig.gridSize + ChartConfig.horPadding,
            bottom - value * ChartConfig.gridSize
            )
        )
    }

    if (dotClicked != Int.MAX_VALUE) {
        drawDotInfo(
            canvas = canvas,
            listDot = listDot,
            witchOne = dotClicked
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
}

fun drawDotInfo(
    canvas: Canvas,
    listDot: List<Offset>,
    witchOne: Int
) {
    val infoStart = listDot[witchOne].x - ChartConfig.infoWidth
    val infoEnd = listDot[witchOne].x + ChartConfig.infoWidth
    val infoTop = listDot[witchOne].y - ChartConfig.infoHeight
    val infoBottom = listDot[witchOne].y - 40f

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
        listDot[witchOne].x - textPaint.textSize,
        infoTop + 50f,
        textPaint
    )
    textPaint.textSize = 30f
    canvas.nativeCanvas.drawText("Some info about",
        infoStart + 20f,
        infoTop + 100f,
        textPaint
    )
    canvas.nativeCanvas.drawText("this dot",
        infoStart + 20f,
        infoTop + 130f,
        textPaint
    )

    canvas.drawCircle(listDot[witchOne], 20f, Paint().apply {
        style = PaintingStyle.Fill
        color = Color.Red
        strokeWidth = 3f
    })
}
