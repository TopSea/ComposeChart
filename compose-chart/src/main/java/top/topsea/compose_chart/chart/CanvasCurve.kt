package top.topsea.compose_chart.chart

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
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
    modifier: Modifier,
    line: Line,
    model: Int,
    xStepSize: Float,
    yStepSize: Float
) {
    val stop = remember { mutableStateOf(0f) }

    val animate by animateFloatAsState(
        targetValue = stop.value,
        animationSpec = tween(durationMillis = 3000, easing = FastOutSlowInEasing)
    )
    Canvas(
        modifier = modifier.fillMaxHeight(),
    ) {
        val yEnd = size.height - CoordinateChart.padding
        val canvas = drawContext.canvas

        line.handleValues(
            model = model,
            xStepSize = xStepSize,
            yStepSize = yStepSize,
            yEnd = yEnd,
            padding = CoordinateChart.padding
        )

        line.drawCurve(canvas, stop, animate)

    }
}
