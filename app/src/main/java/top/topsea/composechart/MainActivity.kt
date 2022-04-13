package top.topsea.composechart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import top.topsea.compose_chart.*
import top.topsea.compose_chart.chart.CanvasCoordinate
import top.topsea.compose_chart.chart.CanvasCurve
import top.topsea.compose_chart.expression.ExpressionGraphic
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
                    val values = remember { mutableStateListOf(0f, 1f, 4f, 4f, 9f, 9f, 4f, 7f) }
                    CanvasCoordinate()

                    CanvasCurve(values = values)

//                    LaunchedEffect(key1 = Unit) {
//                        delay(6000)
//                        values.add(3f)
//                    }

//                    TestChart()
                }
            }
        }
    }
}

@Composable
fun TestChart() {

    val e: Expression = ExpressionBuilder("sin(x) + 2")
        .variables("x")
        .build()
    val list = ExpressionGraphic(e).getLineGraph()
    val values = remember { mutableStateListOf(0f, 1f, 4f, 4f, 4f,4f, 4f, 9f,4f, 4f, 9f, 9f, 4f, 7f)  }

    val info = remember { mutableStateListOf(0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f) }
    val line = LineConfig(
        listValue = values,
        listInfo = info
    )
    val coordinate = CoordinateConfig()
    val chart = ChartConfig(
        coordinate = coordinate,
        line = line,
        chartLayout = ChartConfig.LAYOUT_ALL_POS
    )
    chart.scalable = true
    chart.chartModel = ChartConfig.MODEL_CURVE
    ComposeChart(
        chartConfig = chart
    )
    chart()
    LaunchedEffect(key1 = Unit) {
        while (values.size < 20) {
            delay(2000)
            values.add(values.size + 1f)
        }
        println("gaohai:::${values.last()}")
    }
}

@Composable
fun Test(
    modifier: Modifier
) {
    Canvas(
        modifier = modifier
    ) {

    }
}
