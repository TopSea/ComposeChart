package top.topsea.compose_chart.chart

import android.util.Range
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import top.topsea.compose_chart.ChartConfig

class Line {
    var name: String
    private var mValueList: SnapshotStateList<Float>
    private var step: Float = 1.0f
    var showDot: Boolean = true
    var showValue: Boolean = false
    var showInfo: Boolean = true
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

    fun handleValues(
        gridSize: Float,
        yEnd: Float,
        padding: Float
    ): List<Offset> {
        return List(mValueList.size){ index ->
            Offset(
                index * gridSize * step + padding,
                yEnd - mValueList[index] * gridSize
            )
        }
    }


    private fun drawCurveName(
        canvas: Canvas,
        xEnd: Float,
        textTop: Float,
        linePaint: Paint,
    ) {
        val textPaint = NativePaint().apply {
            color = android.graphics.Color.BLACK
            style = android.graphics.Paint.Style.FILL
            strokeWidth = 1f
            textSize = 30f
        }
        val path = Path()
        path.moveTo(xEnd - 340f, textTop)
        path.lineTo(xEnd - 240f, textTop)
        canvas.drawPath(
            path,
            linePaint.apply {
                style = PaintingStyle.Stroke
                strokeWidth = 5f
            }
        )
        canvas.nativeCanvas.drawText(
            name,
            xEnd - 210f,
            textTop,
            textPaint
        )
    }

    private fun drawDot(
        canvas: Canvas,
        pos: FloatArray,
        listDot: List<Offset>,
        paint: Paint
    ) {
        for (point in listDot) {
            if (point.x > pos[0]) {
                break
            }
            canvas.drawCircle(Offset(point.x, point.y), 7f, paint.apply { style = PaintingStyle.Fill })
        }
    }
}