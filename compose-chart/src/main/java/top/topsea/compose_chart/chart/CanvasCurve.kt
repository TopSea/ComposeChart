package top.topsea.compose_chart.chart

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import kotlinx.coroutines.delay
import top.topsea.compose_chart.ChartConfig

@Composable
fun CanvasLine(
    values: List<Float>
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    val animate = animateOffsetAsState(
        targetValue = offset,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
    )
    val path by remember { mutableStateOf( Path() ) }

    val listDot = mutableListOf<Offset>()
    var canvas: Canvas? = null
    var down by remember { mutableStateOf(false) }
    var lineDown by remember { mutableStateOf(false) }

    Canvas(
        modifier = Modifier.fillMaxSize(),

    ) {
        println("gaohai:::values${values}")
        canvas = drawContext.canvas
        val yEnd = size.height - ChartConfig.verPadding
        values.forEachIndexed { index, value ->
            listDot.add(
                Offset(
                    index * ChartConfig.gridSize.value + ChartConfig.horPadding,
                    yEnd - value * ChartConfig.gridSize.value
                )
            )
        }

        if (down) {
            if ((animate.value.y > listDot.first().y - 1f && animate.value.y < listDot.first().y + 1f) || lineDown) {
                path.lineTo(animate.value.x, animate.value.y)
                canvas!!.drawPath(path, Paint().apply { style = PaintingStyle.Stroke
                    strokeWidth = 5f})
                lineDown = true
            }
        }
    }
    LaunchedEffect(key1 = (listDot.size == values.size) || !down) {
        down = true

        delay(10)
        path.reset()
        path.moveTo(listDot.first().x, listDot.first().y)
        listDot.forEach {
            offset = it
            delay(1000)
        }
    }
}

@Composable
fun CanvasCurve(
    lines: List<Line>
) {
    val stop = remember { mutableStateOf(0f) }

    val animate by animateFloatAsState(
        targetValue = stop.value,
        animationSpec = tween(durationMillis = 3000, easing = FastOutSlowInEasing)
    )
    Canvas(
        modifier = Modifier.fillMaxSize(),
    ) {
        val yEnd = size.height - ChartConfig.verPadding
        val xEnd = size.width - ChartConfig.horPadding
        val canvas = drawContext.canvas

        canvas.drawRect(
            left = size.width - 360f,
            top = 0f,
            right = size.width,
            bottom = 80f * lines.size,
            paint = Paint().apply {
                style = PaintingStyle.Fill
                color = Color.LightGray
            }
        )
        canvas.drawRect(
            left = size.width - 360f,
            top = 0f,
            right = size.width,
            bottom = 80f * lines.size,
            paint = Paint().apply {
                strokeWidth = 2f
                style = PaintingStyle.Stroke
                color = Color.Black
            }
        )

        lines.forEachIndexed { index, line ->
            drawCurve(
                canvas = drawContext.canvas,
                line = line,
                yEnd = yEnd,
                stop = stop,
                animate = animate
            )

            drawCurveName(
                canvas = drawContext.canvas,
                name = line.name,
                xEnd = xEnd + ChartConfig.horPadding,
                textTop = 80f * index + 40f,
                linePaint = line.linePaint
            )
        }
    }
}

fun drawCurve(
    canvas: Canvas,
    line: Line,
    yEnd: Float,
    stop: MutableState<Float>,
    animate: Float
) {
    val listDot = line.handleValues(yEnd, ChartConfig.gridSize.value, ChartConfig.horPadding)
    val srcPath = handleCurvePath(listDot)
    val dstPath = Path()
    val mPathMeasure = android.graphics.PathMeasure()
    mPathMeasure.setPath(srcPath.asAndroidPath(), false)
    stop.value = mPathMeasure.length

    if (mPathMeasure.getSegment(0f, animate, dstPath.asAndroidPath(), true)) {
        //绘制线
        canvas.drawPath(
            path = dstPath,
            paint = line.linePaint.apply {
                style = PaintingStyle.Stroke
                strokeWidth = 3f
            }
        )

        if (line.showDot) {
            val pos = FloatArray(2)
            mPathMeasure.getPosTan(animate, pos, null)
            drawDot(canvas, pos, listDot, line.linePaint)
        }

    }
}

fun drawCurveName(
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

private fun drawDot(
    canvas: Canvas,
    pos: FloatArray,
    listDot: List<Offset>,
    paint: Paint
) {
    for (point in listDot) {
        if (point.x > pos[0]) {
            break
        }
        canvas.drawCircle(Offset(point.x, point.y), 7f, paint.apply { style = PaintingStyle.Fill })
    }
}

private fun handleData(
    values: List<Float>,
    yEnd: Float
): List<Offset> {
    return List(values.size){ index ->
        Offset(
            index * ChartConfig.gridSize.value + ChartConfig.horPadding,
            yEnd - values[index] * ChartConfig.gridSize.value
        )
    }
}

private fun handleCurvePath(
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