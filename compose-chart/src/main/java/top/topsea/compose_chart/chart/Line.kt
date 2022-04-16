package top.topsea.compose_chart.chart

import android.util.Range
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder

class Line {
    var name: String
    private var mValueList: SnapshotStateList<Float>
    private lateinit var mPath: Path
    private lateinit var mListDot: List<Offset>

    //这里的step是在x轴step上的step，如果0.5则表示会在x轴step正中间取一个点
    private var step: Float = 1.0f

    var showDot: Boolean = true
    var showValue: Boolean = false
    var showDotInfo: Boolean = true

    var linePaint: Paint = Paint().apply {
        color = Color.Red
        style = PaintingStyle.Stroke
        strokeWidth = 3f
    }

    //表达式不能动态添加
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

    //值列表可以动态添加
    constructor(name: String, values: SnapshotStateList<Float>) {
        this.name = name
        mValueList = values
    }

    fun drawCurve(
        canvas: Canvas,
        stop: MutableState<Float>,
        animate: Float
    ) {
        val dstPath = Path()
        val mPathMeasure = android.graphics.PathMeasure()
        mPathMeasure.setPath(mPath.asAndroidPath(), false)
        stop.value = mPathMeasure.length

        if (mPathMeasure.getSegment(0f, animate, dstPath.asAndroidPath(), true)) {
            //绘制线
            canvas.drawPath(
                path = dstPath,
                paint = linePaint.apply {
                    style = PaintingStyle.Stroke
                    strokeWidth = 3f
                }
            )

            if (showDot) {
                val pos = FloatArray(2)
                mPathMeasure.getPosTan(animate, pos, null)
                drawDot(canvas, pos, linePaint)
            }

        }
    }

    fun handleValues(
        xStepSize: Float,
        yStepSize: Float,
        yEnd: Float,
        padding: Float
    ) {
        mListDot = List(mValueList.size){ index ->
            Offset(
                index * xStepSize * step + padding,
                yEnd - mValueList[index] * yStepSize
            )
        }
        mPath = Path()
        val smooth = 0.2f

        var prePreviousPointX = Float.NaN
        var prePreviousPointY = Float.NaN
        var previousPointX = Float.NaN
        var previousPointY = Float.NaN
        var currentPointX = Float.NaN
        var currentPointY = Float.NaN
        var nextPointX: Float
        var nextPointY: Float

        mListDot.forEachIndexed { index, offset ->
            if (currentPointX.isNaN()) {
                currentPointX = offset.x
                currentPointY = offset.y
            }
            if (previousPointX.isNaN()) {
                //是否是第一个点
                if (index > 0) {
                    previousPointX = mListDot[index - 1].x
                    previousPointY = mListDot[index - 1].y
                } else {
                    //是的话就用当前点表示上一个点
                    previousPointX = currentPointX
                    previousPointY = currentPointY
                }
            }
            if (prePreviousPointX.isNaN()) {
                //是否是前两个点
                if (index > 1) {
                    prePreviousPointX = mListDot[index - 2].x
                    prePreviousPointY = mListDot[index - 2].y
                } else {
                    //是的话就用当前点表示上上个点
                    prePreviousPointX = previousPointX
                    prePreviousPointY = previousPointY
                }
            }

            // 判断是不是最后一个点了
            if (offset == mListDot.last()) {
                //是的话就用当前点表示下一个点
                nextPointX = currentPointX
                nextPointY = currentPointY
            } else {
                nextPointX = mListDot[index + 1].x
                nextPointY = mListDot[index + 1].y
            }

            if (offset == mListDot.first()) {
                // 将Path移动到开始点
                mPath.moveTo(currentPointX, currentPointY)
            } else {// 求出控制点坐标
                val firstDiffX = currentPointX - prePreviousPointX
                var firstDiffY = currentPointY - prePreviousPointY
                val secondDiffX = nextPointX - previousPointX
                var secondDiffY = nextPointY - previousPointY

                if (nextPointY - currentPointY == 0f) {
                    secondDiffY = 0f
                }
                if (currentPointY - previousPointY == 0f) {
                    firstDiffY = 0f
                    secondDiffY *= 0.3f
                }

                val ctl1X: Float = previousPointX + smooth * firstDiffX
                val ctl1Y: Float = previousPointY + smooth * firstDiffY
                val ctl2X: Float = currentPointX - smooth * secondDiffX
                val ctl2Y: Float = currentPointY - smooth * secondDiffY

                mPath.cubicTo(
                    ctl1X, ctl1Y, ctl2X, ctl2Y,
                    currentPointX, currentPointY
                )
            }

            // 更新值,
            prePreviousPointX = previousPointX
            prePreviousPointY = previousPointY
            previousPointX = currentPointX
            previousPointY = currentPointY
            currentPointX = nextPointX
            currentPointY = nextPointY
        }
    }

    private fun drawDot(
        canvas: Canvas,
        pos: FloatArray,
        paint: Paint
    ) {
        for (point in mListDot) {
            println("gaohai:::$point")
            if (point.x > pos[0]) {
                break
            }
            canvas.drawCircle(Offset(point.x, point.y), 7f, paint.apply { style = PaintingStyle.Fill })
        }
    }
}