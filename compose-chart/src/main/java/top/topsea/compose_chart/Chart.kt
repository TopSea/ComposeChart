package top.topsea.compose_chart

import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder

fun chart() {
    val e: Expression = ExpressionBuilder("3 * sin(y) - 2 / (x - 2)")
        .variables("x", "y")
        .build()
        .setVariable("x", 2.3)
        .setVariable("y", Math.PI)
    val result: Double = e.evaluate()
    println("gaohai:::result:$result")
}