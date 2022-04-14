package top.topsea.composechart

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Range
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import top.topsea.compose_chart.*
import top.topsea.compose_chart.chart.CanvasCoordinate
import top.topsea.compose_chart.chart.*
import top.topsea.compose_chart.expression.ExpressionGraphic
import top.topsea.composechart.ui.theme.ComposeChartTheme
import kotlin.random.Random

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
                    val line1 = Line(
                        name = "sin(x) + 1",
                        xRange = Range(0.0, 10.0),
                        step = 1.5f
                    )
                    val list = remember { mutableStateListOf(0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f) }
                    val line2 = Line(
                        name = "sin(x) + 1",
                        list
                    )

                    CanvasCoordinate()
                    CanvasCurve(line = line1)


//                    LaunchedEffect(key1 = Unit) {
//                        delay(6000)
//                        while (list.size < 10) {
//                            delay(3000)
//                            line1.getValueList().add(Random.nextFloat() * 18)
//                            println("gaohai:::Random ${line1.getValueList().last()}")
//                        }
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
