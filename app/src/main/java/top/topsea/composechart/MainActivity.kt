package top.topsea.composechart

import android.os.Bundle
import android.util.Range
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.NativePaint
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
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
//                    val line1 = Line(
//                        name = "sin(x) + 1",
//                        xRange = Range(0.0, 10.0),
//                        step = 0.5f
//                    )
//                    line1.linePaint.apply {
//                        color = Color.Blue
//                    }
//                    line1.showDot = false
//                    val list = remember { mutableStateListOf(0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f) }
//                    val line2 = Line(
//                        name = "cos(x) + 1",
//                        xRange = Range(0.0, 10.0),
//                        step = 0.5f
//                    )
//                    val line3 = Line(
//                        name = "cos(x) + 5",
//                        xRange = Range(0.0, 10.0),
//                        step = 0.5f
//                    )
//                    line3.linePaint.apply {
//                        color = Color.Yellow
//                    }
//
//                    val lines = listOf<Line>(line1, line2, line3)
//
//                    CanvasCoordinate()
//                    CanvasCurve(lines = lines)


//                    LaunchedEffect(key1 = Unit) {
//                        delay(6000)
//                        while (list.size < 10) {
//                            delay(3000)
//                            line1.getValueList().add(Random.nextFloat() * 18)
//                            println("gaohai:::Random ${line1.getValueList().last()}")
//                        }
//                    }

//                    TestChart()
                    
                    Test(modifier = Modifier.fillMaxSize())
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
    val stop = remember { mutableStateOf(0f) }

    val animate by animateFloatAsState(
        targetValue = stop.value,
        animationSpec = tween(durationMillis = 3000, easing = FastOutSlowInEasing)
    )


    val line1 = Line(
        name = "sin(x) + 1",
        xRange = Range(0.0, 10.0),
        step = 0.5f
    )
    line1.linePaint.apply {
        color = Color.Blue
    }
    line1.showDot = false
    line1.animate = animate

    val line2 = Line(
        name = "cos(x) + 1",
        xRange = Range(0.0, 5.0),
        step = 0.5f
    )
    val line3 = Line(
        name = "cos(x) + 5",
        xRange = Range(0.0, 10.0),
        step = 0.5f
    )
    line3.linePaint.apply {
        color = Color.Yellow
    }
    val lines = listOf<Line>(line1, line2, line3)
    Canvas(
        modifier = modifier
    ) {

        val chart = LineChart(
            canvas = drawContext.canvas,
            lines = lines,
            height = size.height,
            width = size.width
        )

        chart.axisUnit = arrayOf("千元", "吨")
        chart.model = CoordinateChart.MODEL_ALL_POS
        chart.drawChart(
            stop = stop,
            animate = animate
        )
    }
    LaunchedEffect(key1 = Unit) {
        delay(2000)
        while (line2.getValueList().size < 20) {
            delay(200)
            line2.getValueList().add(Random.nextFloat() * 18)
        }
    }
}
