package top.topsea.composechart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import top.topsea.composechart.ui.theme.ComposeChartTheme

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
fun Greeting() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawChartCoordinate(
            canvas = drawContext.canvas,
            height = size.height,
            width = size.width
        )
        drawLine(
            canvas = drawContext.canvas,
            height = size.height,
            values = listOf(0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f)
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