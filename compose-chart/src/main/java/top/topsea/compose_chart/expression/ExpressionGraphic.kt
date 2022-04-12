package top.topsea.compose_chart.expression

import android.util.Range
import androidx.compose.runtime.snapshots.SnapshotStateList
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder

data class ExpressionGraphic(
    val exp: Expression,
    var xRange: Range<Double> = Range(-10.0, 10.0),
    var step: Double = 0.2
) {
    fun getLineGraph(): SnapshotStateList<Float> {
        val resultList = SnapshotStateList<Float>()
        var i = xRange.lower!!
        while (i < xRange.upper) {
            exp.setVariable("x", i)
            resultList.add(exp.evaluate().toFloat())
            i += step
        }
        return resultList
    }
}