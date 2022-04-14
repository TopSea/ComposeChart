package top.topsea.compose_chart.chart

import android.util.Range
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import top.topsea.compose_chart.ChartConfig

class Line {
    var name: String
    private var mValueList: SnapshotStateList<Float>
    private var step: Float = 1.0f
    var linePaint: Paint = Paint().apply {
        color = Color.Red
        style = PaintingStyle.Stroke
        strokeWidth = 3f
    }

    constructor(
        name: String,
        xRange: Range<Double> = Range(-10.0, 10.0),
        step: Float
    ) {
        this.name = name
        this.step = step
        val e: Expression = ExpressionBuilder(name)
            .variables("x")
            .build()
        mValueList = SnapshotStateList<Float>()
        var i = xRange.lower!!
        while (i < xRange.upper) {
            e.setVariable("x", i)
            mValueList.add(e.evaluate().toFloat())
            i += step
        }
    }
    constructor(name: String, values: SnapshotStateList<Float>) {
        this.name = name
        mValueList = values
    }

    fun getValueList(): List<Float> {
        return mValueList
    }

    fun handleValues(
        yEnd: Float
    ): List<Offset> {
        return List(mValueList.size){ index ->
            Offset(
                index * ChartConfig.gridSize.value * step + ChartConfig.horPadding,
                yEnd - mValueList[index] * ChartConfig.gridSize.value
            )
        }
    }
}