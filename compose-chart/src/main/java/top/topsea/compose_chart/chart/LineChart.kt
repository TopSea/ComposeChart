package top.topsea.compose_chart.chart

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*

class LineChart(
    val canvas: Canvas,
    val lines: List<Line>,
    val height: Float,
    val width: Float,
) : CoordinateChart() {
    override var withGrid: Boolean = true
    override var withArrow: Boolean = true
    override var withText: Boolean = true

    override var axisUnit: Array<String>? = null

    var gridSize = mutableStateOf(100f)
    var withCoordinate: Boolean = true

//    constructor(
//        lines: List<Line>,
//        canvas: Canvas,
//        height: Float,
//        width: Float
//    ) : this(
//        lines = lines,
//        canvas = canvas,
//        height = height,
//        width = width,
//    )
override fun drawChart(
) {}

    fun drawChart(
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

        lines.forEach { line ->
            line.handleValues(
                xStepSize = xStepSize,
                yStepSize = yStepSize,
                yEnd = yEnd,
                padding = padding
            )

           line.drawCurve(
               canvas = canvas,
               stop, animate
           )
        }
    }


    override fun export() {
        TODO("Not yet implemented")
    }
}