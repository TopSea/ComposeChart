package top.topsea.compose_chart.chart

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

    override fun drawChart() {
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
            println("gaohai:::something1")
            val listDot = line.handleValues(gridSize = gridSize.value, yEnd, padding)
            val path = handlePath(listDot)

            val dstPath = Path()
            val mPathMeasure = android.graphics.PathMeasure()
            mPathMeasure.setPath(path.asAndroidPath(), false)


            if (mPathMeasure.getSegment(0f, mPathMeasure.length, dstPath.asAndroidPath(), true)) {
                //绘制线
                canvas.drawPath(
                    path = dstPath,
                    paint = line.linePaint.apply {
                        style = PaintingStyle.Stroke
                        strokeWidth = 3f
                    }
                )
            }
        }
    }

    private fun handlePath(
        listDot: List<Offset>
    ): Path {
        val srcPath = Path()
        val smooth = 0.2f

        var prePreviousPointX = Float.NaN
        var prePreviousPointY = Float.NaN
        var previousPointX = Float.NaN
        var previousPointY = Float.NaN
        var currentPointX = Float.NaN
        var currentPointY = Float.NaN
        var nextPointX: Float
        var nextPointY: Float

        listDot.forEachIndexed { index, offset ->
            if (currentPointX.isNaN()) {
                currentPointX = offset.x
                currentPointY = offset.y
            }
            if (previousPointX.isNaN()) {
                //是否是第一个点
                if (index > 0) {
                    previousPointX = listDot[index - 1].x
                    previousPointY = listDot[index - 1].y
                } else {
                    //是的话就用当前点表示上一个点
                    previousPointX = currentPointX
                    previousPointY = currentPointY
                }
            }
            if (prePreviousPointX.isNaN()) {
                //是否是前两个点
                if (index > 1) {
                    prePreviousPointX = listDot[index - 2].x
                    prePreviousPointY = listDot[index - 2].y
                } else {
                    //是的话就用当前点表示上上个点
                    prePreviousPointX = previousPointX
                    prePreviousPointY = previousPointY
                }
            }

            // 判断是不是最后一个点了
            if (offset == listDot.last()) {
                //是的话就用当前点表示下一个点
                nextPointX = currentPointX
                nextPointY = currentPointY
            } else {
                nextPointX = listDot[index + 1].x
                nextPointY = listDot[index + 1].y
            }

            if (offset == listDot.first()) {
                // 将Path移动到开始点
                srcPath.moveTo(currentPointX, currentPointY)
            } else {// 求出控制点坐标
                val firstDiffX = currentPointX - prePreviousPointX
                var firstDiffY = currentPointY - prePreviousPointY
                val secondDiffX = nextPointX - previousPointX
                var secondDiffY = nextPointY - previousPointY

                if (nextPointY - currentPointY == 0f) {
                    secondDiffY = 0f
                }
                if (currentPointY - previousPointY == 0f) {
                    firstDiffY = 0f
                    secondDiffY *= 0.3f
                }

                val ctl1X: Float = previousPointX + smooth * firstDiffX
                val ctl1Y: Float = previousPointY + smooth * firstDiffY
                val ctl2X: Float = currentPointX - smooth * secondDiffX
                val ctl2Y: Float = currentPointY - smooth * secondDiffY

                srcPath.cubicTo(
                    ctl1X, ctl1Y, ctl2X, ctl2Y,
                    currentPointX, currentPointY
                )
            }

            // 更新值,
            prePreviousPointX = previousPointX
            prePreviousPointY = previousPointY
            previousPointX = currentPointX
            previousPointY = currentPointY
            currentPointX = nextPointX
            currentPointY = nextPointY
        }
        return srcPath
    }

    override fun export() {
        TODO("Not yet implemented")
    }
}