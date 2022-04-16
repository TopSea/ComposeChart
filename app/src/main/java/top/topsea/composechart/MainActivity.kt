package top.topsea.composechart

import android.os.Bundle
import android.util.Range
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.NativePaint
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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
//                    TestChart()

                    LazyColumn(
                        modifier = Modifier.fillMaxSize().background(Color.LightGray)
                    ) {
                        item {
                            Chart1()
                            Chart2()
                            Chart3()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Chart1() {
    var input by remember { mutableStateOf("") }

    val list = remember { mutableStateListOf(0f, 3f, 5f, 3f, 1f, 2f, 0f, 1f) }
    val line2 = Line(
        name = "line2",
        values = list
    )
    val lines = listOf<Line>( line2,)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(vertical = 16.dp, horizontal = 8.dp)
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .height(50.dp)
                        .padding(horizontal = 8.dp),
                    value = input,
                    onValueChange = { str -> input = str },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Button(onClick = { list.add(input.toFloat()) }) {
                    Text(text = "添加")
                }
            }

            Test(lines = lines)
        }
    }
}

@Composable
fun Chart2() {
//    val line1 = Line(
//        name = "sin(x) + 2",
//        xRange = Range(0.0, 8.0),
//        step = 0.5f
//    )
//    line1.linePaint.color = Color.Green
    val line3 = Line(
        name = "cos(x) + 5",
        xRange = Range(0.0, 8.0),
        step = 0.5f
    )
    line3.linePaint.color = Color.Blue
    val lines = listOf<Line>( line3)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(vertical = 16.dp, horizontal = 8.dp)
    ) {

        Test(lines = lines)
    }
}

@Composable
fun Chart3() {
//    val line1 = Line(
//        name = "sin(x) + 2",
//        xRange = Range(0.0, 8.0),
//        step = 0.5f
//    )
//    line1.linePaint.color = Color.Green
    val line3 = Line(
        name = "cos(x) + 5",
        xRange = Range(0.0, 8.0),
        step = 0.5f
    )
    line3.linePaint.color = Color.Blue

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(vertical = 16.dp, horizontal = 8.dp)
    ) {
        CanvasCurve(line = line3, xStepSize = 100f, yStepSize = 100f)
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
    }
}

@Composable
fun Test(
    lines: List<Line>
) {
    val stop = remember { mutableStateOf(0f) }

    val animate by animateFloatAsState(
        targetValue = stop.value,
        animationSpec = tween(durationMillis = 3000, easing = FastOutSlowInEasing)
    )

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            val chart = LineChart(
                canvas = drawContext.canvas,
                lines = lines,
                height = size.height,
                width = size.width
            )

//        chart.axisUnit = arrayOf("千元", "吨")
            chart.model = CoordinateChart.MODEL_ALL_POS
            chart.drawChart(
                stop = stop,
                animate = animate
            )
        }
    }
}
