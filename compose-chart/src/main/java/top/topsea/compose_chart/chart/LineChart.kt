package top.topsea.compose_chart.chart

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import top.topsea.compose_chart.ChartConfig

class LineChart(
    val lines: List<Line>,
) : CoordinateChart() {
    override var withGrid: Boolean = true
    override var withArrow: Boolean = true
    override var withText: Boolean = true

    override var axisUnit: Array<String>? = null

    var gridSize = mutableStateOf(100f)
    var withCoordinate: Boolean = true
    var withChartInfo: Boolean = true
    var scrollable: Boolean = true

    var height: Float = 0f
    var width: Float = 0f

    constructor(line: Line, ) : this(lines = listOf(line),)


override fun drawChart(
) {}

    fun drawChart(
        canvas: Canvas,
        stop: MutableState<Float>,
        animate: Float
    ) {
        drawCoordinate(
            canvas = canvas,
            axisPaint = Paint().apply {
                style = PaintingStyle.Stroke
                color = Color.LightGray
                strokeWidth = 2f
            },
            textPaint = NativePaint().apply {
                color = android.graphics.Color.BLACK
                style = android.graphics.Paint.Style.FILL
                strokeWidth = 1f
                textSize = 24f
            },
            gridSize = gridSize.value,
            height = height,
            width = width,
        )

        if (withChartInfo) {
            canvas.drawRect(
                left = width - 360f,
                top = 0f,
                right = width,
                bottom = 80f * lines.size,
                paint = Paint().apply {
                    style = PaintingStyle.Fill
                    color = Color.LightGray
                }
            )
            canvas.drawRect(
                left = width - 360f,
                top = 0f,
                right = width,
                bottom = 80f * lines.size,
                paint = Paint().apply {
                    strokeWidth = 2f
                    style = PaintingStyle.Stroke
                    color = Color.Black
                }
            )
        }

        lines.forEachIndexed { index, line ->
            line.handleValues(
                model = model,
                xStepSize = xStepSize,
                yStepSize = yStepSize,
                yEnd = yEnd,
                padding = padding
            )


            line.drawCurve(
               canvas = canvas,
               stop, animate
            )

            if (withChartInfo) {
                drawChartInfo(
                    canvas = canvas,
                    name = line.name,
                    xEnd = xEnd + ChartConfig.horPadding,
                    textTop = 80f * index + 40f,
                    linePaint = line.linePaint
                )
            }
        }
    }

    fun drawChartInfo(
        canvas: Canvas,
        name: String,
        xEnd: Float,
        textTop: Float,
        linePaint: Paint,
    ) {
        val textPaint = NativePaint().apply {
            color = android.graphics.Color.BLACK
            style = android.graphics.Paint.Style.FILL
            strokeWidth = 1f
            textSize = 30f
        }
        val path = Path()
        path.moveTo(xEnd - 340f, textTop)
        path.lineTo(xEnd - 240f, textTop)
        canvas.drawPath(
            path,
            linePaint.apply {
                style = PaintingStyle.Stroke
                strokeWidth = 5f
            }
        )
        canvas.nativeCanvas.drawText(
            name,
            xEnd - 210f,
            textTop,
            textPaint
        )
    }

    override fun export() {
        TODO("Not yet implemented")
    }
}