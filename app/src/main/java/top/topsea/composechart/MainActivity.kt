package top.topsea.composechart

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.topsea.composechart.ui.theme.ComposeChartTheme
import kotlin.math.floor

class MainActivity : ComponentActivity() {
    @SuppressLint("MutableCollectionMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeChartTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val values = remember { mutableStateListOf(0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f) }

                    val info = remember { mutableStateListOf(0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f) }
                    val line = LineConfig(
                        listValue = values,
                        listInfo = info
                    )
                    val coordinate = CoordinateConfig()
                    val chart = ChartConfig(
                        coordinate = coordinate,
                        line = line
                    )
                    chart.scalable = true
                    Greeting(
                        chartConfig = chart
                    )


//                    LaunchedEffect(key1 = Unit) {
//                        while (values.size < 20) {
//                            delay(2000)
//                            values.add(values.size + 1f)
//                        }
//                        println("gaohai:::${values.last()}")
//                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(
    chartConfig: ChartConfig
) {
    var dotClicked: MutableState<Int>? = null
    if (chartConfig.line.withInfo) {
        dotClicked = remember { mutableStateOf(Int.MAX_VALUE) }
    }
    val coordinateConfig = chartConfig.coordinate
    val lineConfig = chartConfig.line

    var scrollState:ScrollState? = null
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
            .horizontalScroll(scrollState!!)
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
            drawLine(
                canvas = drawContext.canvas,
                height = size.height,
                width = size.width,
                lineConfig = lineConfig,
                values = values,
                dotClicked = dotClicked
            )
            drawLine(
                color = Color.Blue,
                start = Offset.Zero,
                end = Offset(size.width, size.height)
            )
        }
//        Text(text = "123")
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
        scrollable(scrollState!!, Orientation.Horizontal)
    } else {
        Modifier
    }
)

private fun Modifier.setChartDotClickable(chartConfig: ChartConfig, dotClicked: MutableState<Int>?) = this.then(
    if (chartConfig.dotClickable) {
        val values = chartConfig.line.listValue
        pointerInput(Unit) {
            detectTapGestures(
                onPress = { /* Called when the gesture starts */ },
                onDoubleTap = { /* Called on Double Tap */ },
                onLongPress = { /* Called on Long Press */ },
                onTap = { offset ->
                    val bottom = size.height - ChartConfig.verPadding

                    values.forEachIndexed { index, _ ->
                        val dotSizeX = IntRange(
                            (ChartConfig.horPadding + index * ChartConfig.gridSize.value - 16f).toInt(),
                            (ChartConfig.horPadding + index * ChartConfig.gridSize.value + 16f).toInt()
                        )
                        val dotSizeY = IntRange(
                            (bottom - values[index] * ChartConfig.gridSize.value - 16f).toInt(),
                            (bottom - values[index] * ChartConfig.gridSize.value + 16f).toInt()
                        )
                        if (offset.x.toInt() in dotSizeX && offset.y.toInt() in dotSizeY) {
                            dotClicked!!.value = index
                        }
                    }
                }
            )
        }
    } else {
        Modifier
    }
)
