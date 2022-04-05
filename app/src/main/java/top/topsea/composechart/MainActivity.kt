package top.topsea.composechart

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.debugInspectorInfo
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
                    val values = remember { mutableStateOf(mutableListOf(0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f,)) }
                    val info = remember { mutableStateOf(mutableListOf(0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f,)) }
                    val line = LineConfig(
                        listValue = values,
                        listInfo = info
                    )
                    val coordinate = CoordinateConfig()
                    val chart = ChartConfig(
                        coordinate = coordinate,
                        line = line
                    )
                    chart.scalable = false
                    Greeting(
                        chartConfig = chart
                    )
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

    var state: TransformableState? = null
    if (chartConfig.scalable) {
        state = rememberTransformableState { zoomChange, _, _ ->
            if (ChartConfig.gridSize.value in 30f..200f) {
                ChartConfig.gridSize.value = ChartConfig.gridSize.value * zoomChange
            }
            if (ChartConfig.gridSize.value < 30f) {
                ChartConfig.gridSize.value = 30f
            }else if (ChartConfig.gridSize.value > 200f){
                ChartConfig.gridSize.value = 200f
            }
        }
    }

    val values = chartConfig.line.listValue

    Canvas(
        modifier = Modifier.fillMaxSize().setChartScalable(chartConfig, state)
            .setChartDotClickable(chartConfig, dotClicked)
    ) {
        drawChartCoordinate(
            canvas = drawContext.canvas,
            height = size.height,
            width = size.width
        )
        drawLine(
            canvas = drawContext.canvas,
            height = size.height,
            width = size.width,
            values = values.value,
            dotClicked = dotClicked
        )
        drawLine(
            color = Color.Blue,
            start = Offset.Zero,
            end = Offset(size.width, size.height)
        )
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

private fun Modifier.setChartDotClickable(chartConfig: ChartConfig, dotClicked: MutableState<Int>?) = this.then(
    if (chartConfig.dotClickable) {
        val values = chartConfig.line.listValue
        pointerInput(Unit) {
            detectTapGestures(
                onPress = { /* Called when the gesture starts */ },
                onDoubleTap = { /* Called on Double Tap */ },
                onLongPress = { /* Called on Long Press */ },
                onTap = { offset ->
                    val xLines = floor(size.height / ChartConfig.gridSize.value).toInt()
                    val bottom = (xLines - 1) * ChartConfig.gridSize.value + ChartConfig.verPadding

                    values.value.forEachIndexed { index, _ ->
                        val dotSizeX = IntRange(
                            (ChartConfig.horPadding + index * ChartConfig.gridSize.value - 16f).toInt(),
                            (ChartConfig.horPadding + index * ChartConfig.gridSize.value + 16f).toInt()
                        )
                        val dotSizeY = IntRange(
                            (bottom - values.value[index] * ChartConfig.gridSize.value - 16f).toInt(),
                            (bottom - values.value[index] * ChartConfig.gridSize.value + 16f).toInt()
                        )
                        if (offset.x.toInt() in dotSizeX && offset.y.toInt() in dotSizeY) {
                            dotClicked!!.value = index
                            println("gaohai:::clicked:$index")
                        }
                    }
                }
            )
        }
    } else {
        Modifier
    }
)
