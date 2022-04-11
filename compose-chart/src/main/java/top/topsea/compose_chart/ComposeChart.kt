package top.topsea.compose_chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp


@Composable
fun ComposeChart(
    chartConfig: ChartConfig
) {
    var dotClicked: MutableState<Int>? = null
    if (chartConfig.line.withInfo) {
        dotClicked = remember { mutableStateOf(Int.MAX_VALUE) }
    }
    val coordinateConfig = chartConfig.coordinate
    val lineConfig = chartConfig.line

    var scrollState: ScrollState? = null
    if (chartConfig.scrollable) {
        scrollState = rememberScrollState()
    }
    var state: TransformableState? = null
    if (chartConfig.scalable) {
        state = rememberTransformableState { zoomChange, _, _ ->
            if (coordinateConfig.gridSize.value.toInt() in chartConfig.scaleLimit!!) {
                ChartConfig.gridSize.value = ChartConfig.gridSize.value * zoomChange
            }
            if (ChartConfig.gridSize.value < 50f) {
                ChartConfig.gridSize.value = 50f
            }else if (ChartConfig.gridSize.value > 200f){
                ChartConfig.gridSize.value = 200f
            }
        }
    }

    val values = chartConfig.line.listValue

    Row(
        modifier = Modifier
            .fillMaxSize()
            .setChartScrollable(chartConfig, scrollState)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxHeight()
                .requiredWidth(800.dp)
                .setChartScalable(chartConfig, state)
                .setChartDotClickable(chartConfig, dotClicked)
        ) {
            drawChartCoordinate(
                canvas = drawContext.canvas,
                height = size.height,
                width = size.width,
                chartLayout = chartConfig.chartLayout,
                coordinateConfig = coordinateConfig
            )
            when (chartConfig.chartModel) {
                ChartConfig.MODEL_LINEAR -> {
                    drawLine(
                        canvas = drawContext.canvas,
                        height = size.height,
                        width = size.width,
                        chartLayout = chartConfig.chartLayout,
                        lineConfig = lineConfig,
                        values = values,
                        dotClicked = dotClicked
                    )
                }
                ChartConfig.MODEL_CURVE -> {
                    drawCurve(
                        canvas = drawContext.canvas,
                        values = values,
                        height = size.height,
                        width = size.width,
                        chartLayout = chartConfig.chartLayout,
                        lineConfig = lineConfig,
                        dotClicked = dotClicked
                    )
                }
                else -> {}
            }
        }
    }
}

private fun Modifier.setChartScalable(chartConfig: ChartConfig,
                                      state: TransformableState?) = this.then(
    if (chartConfig.scalable) {
        transformable(state!!)
    } else {
        Modifier
    }
)

private fun Modifier.setChartScrollable(chartConfig: ChartConfig,
                                        scrollState: ScrollState?) = this.then(
    if (chartConfig.scalable) {
        horizontalScroll(scrollState!!)
    } else {
        Modifier
    }
)

private fun Modifier.setChartDotClickable(
    chartConfig: ChartConfig,
    dotClicked: MutableState<Int>?
) = this.then(
    if (chartConfig.dotClickable) {
        val values = chartConfig.line.listValue
        pointerInput(Unit) {
            detectTapGestures(
                onPress = { /* Called when the gesture starts */ },
                onDoubleTap = { /* Called on Double Tap */ },
                onLongPress = { /* Called on Long Press */ },
                onTap = { offset ->

                    val xEnd = size.width - ChartConfig.horPadding
                    val yEnd = size.height - ChartConfig.verPadding

                    when (chartConfig.chartLayout) {
                        ChartConfig.LAYOUT_ALL_POS -> {
                            values.forEachIndexed { index, _ ->
                                val dotSizeX = IntRange(
                                    (ChartConfig.horPadding + index * ChartConfig.gridSize.value - 16f).toInt(),
                                    (ChartConfig.horPadding + index * ChartConfig.gridSize.value + 16f).toInt()
                                )
                                val dotSizeY = IntRange(
                                    (yEnd - values[index] * ChartConfig.gridSize.value - 16f).toInt(),
                                    (yEnd - values[index] * ChartConfig.gridSize.value + 16f).toInt()
                                )
                                if (offset.x.toInt() in dotSizeX && offset.y.toInt() in dotSizeY) {
                                    dotClicked!!.value = index
                                }
                            }
                        }
                        ChartConfig.LAYOUT_ALL -> {
                            val xLines = (yEnd / ChartConfig.gridSize.value).toInt()
                            val yLines = (xEnd / ChartConfig.gridSize.value).toInt()
                            //x轴的y位置
                            val xAxisPosition = yEnd - xLines / 2 * ChartConfig.gridSize.value
                            //y轴的x位置
                            val yAxisPosition = ChartConfig.horPadding + yLines / 2 * ChartConfig.gridSize.value
                            values.forEachIndexed { index, _ ->
                                val dotSizeX = IntRange(
                                    (yAxisPosition + index * ChartConfig.gridSize.value - 16f).toInt(),
                                    (yAxisPosition + index * ChartConfig.gridSize.value + 16f).toInt()
                                )
                                val dotSizeY = IntRange(
                                    (xAxisPosition - values[index] * ChartConfig.gridSize.value - 16f).toInt(),
                                    (xAxisPosition - values[index] * ChartConfig.gridSize.value + 16f).toInt()
                                )
                                if (offset.x.toInt() in dotSizeX && offset.y.toInt() in dotSizeY) {
                                    dotClicked!!.value = index
                                }
                            }
                        }
                        ChartConfig.LAYOUT_X_POS -> {
                            val yLines = (xEnd / ChartConfig.gridSize.value).toInt()
                            //y轴的x位置
                            val yAxisPosition = ChartConfig.horPadding + yLines / 2 * ChartConfig.gridSize.value
                            values.forEachIndexed { index, _ ->
                                val dotSizeX = IntRange(
                                    (yAxisPosition + index * ChartConfig.gridSize.value - 16f).toInt(),
                                    (yAxisPosition + index * ChartConfig.gridSize.value + 16f).toInt()
                                )
                                val dotSizeY = IntRange(
                                    (yEnd - values[index] * ChartConfig.gridSize.value - 16f).toInt(),
                                    (yEnd - values[index] * ChartConfig.gridSize.value + 16f).toInt()
                                )
                                if (offset.x.toInt() in dotSizeX && offset.y.toInt() in dotSizeY) {
                                    dotClicked!!.value = index
                                }
                            }
                        }
                    }
                }
            )
        }
    } else {
        Modifier
    }
)
