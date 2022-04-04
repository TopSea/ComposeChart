package top.topsea.composechart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import top.topsea.composechart.ui.theme.ComposeChartTheme
import kotlin.math.floor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeChartTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting(
    values: List<Float> = listOf(0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, )
) {
    var dotClicked by remember { mutableStateOf(Int.MAX_VALUE) }

    var scale by remember { mutableStateOf(1f) }

    val state = rememberTransformableState { zoomChange, _, _ ->
        scale *= zoomChange
        if (ChartConfig.gridSize.value in 30f..200f) {
            ChartConfig.gridSize.value = ChartConfig.gridSize.value * scale
        }
        if (ChartConfig.gridSize.value < 30f) {
            ChartConfig.gridSize.value = 30f
        }else if (ChartConfig.gridSize.value > 200f){
            ChartConfig.gridSize.value = 200f
        }
    }
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .transformable(state = state)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { /* Called when the gesture starts */ },
                    onDoubleTap = { /* Called on Double Tap */ },
                    onLongPress = { /* Called on Long Press */ },
                    onTap = { offset ->
                        val xLines = floor(size.height / ChartConfig.gridSize.value).toInt()
                        val bottom = (xLines - 1) * ChartConfig.gridSize.value + ChartConfig.verPadding

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
                                dotClicked = index
                                println("gaohai:::clicked:$index")
                            }
                        }
                    }
                )
            }
    ) {
        println("gaohai:::$scale")
        drawChartCoordinate(
            canvas = drawContext.canvas,
            height = size.height,
            width = size.width
        )
        drawLine(
            canvas = drawContext.canvas,
            height = size.height,
            width = size.width,
//            values = listOf(0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f)
            values = values,
            dotClicked = dotClicked
        )
        drawLine(
            color = Color.Blue,
            start = Offset.Zero,
            end = Offset(size.width, size.height)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeChartTheme {
        Greeting()
    }
}