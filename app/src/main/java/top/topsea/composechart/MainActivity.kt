package top.topsea.composechart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
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
                    val values = remember { mutableStateListOf(0f, 1f, 2f, 1f, 1f, 5f, 6f, 3f) }

                    val info = remember { mutableStateListOf(0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f) }
                    val line = LineConfig(
                        listValue = values,
                        listInfo = info
                    )
                    val coordinate = CoordinateConfig()
                    val chart = ChartConfig(
                        coordinate = coordinate,
                        line = line,
                        chartLayout = ChartConfig.LAYOUT_ALL
                    )
                    chart.scalable = true
                    ComposeChart(
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
