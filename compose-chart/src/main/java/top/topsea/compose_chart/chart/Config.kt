package top.topsea.compose_chart.chart

import androidx.compose.runtime.compositionLocalOf

data class Config(
    var padding: Float = 60f,

) {
}

val LocalChartConfig = compositionLocalOf { Config() }