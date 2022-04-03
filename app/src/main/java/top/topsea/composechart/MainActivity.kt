package top.topsea.composechart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { /* Called when the gesture starts */ },
                    onDoubleTap = { /* Called on Double Tap */ },
                    onLongPress = { /* Called on Long Press */ },
                    onTap = { offset ->
                        val xLines = floor(size.height / ChartConfig.gridSize).toInt()
                        val bottom = (xLines - 1) * ChartConfig.gridSize + ChartConfig.verPadding

                        values.forEachIndexed{ index, _ ->
                            val dotSizeX = IntRange(
                                (ChartConfig.horPadding + index * ChartConfig.gridSize - 16f).toInt(),
                                (ChartConfig.horPadding + index * ChartConfig.gridSize + 16f).toInt()
                            )
                            val dotSizeY = IntRange(
                                (bottom - values[index] * ChartConfig.gridSize - 16f).toInt(),
                                (bottom - values[index] * ChartConfig.gridSize + 16f).toInt()
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
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeChartTheme {
        Greeting()
    }
}