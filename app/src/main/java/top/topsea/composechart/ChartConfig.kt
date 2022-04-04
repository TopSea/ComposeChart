package top.topsea.composechart

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class ChartConfig(
    var gridSize: Int
) {
    companion object {
        const val horPadding = 80f
        const val verPadding = 60f
        var gridSize = mutableStateOf(100f)
        const val infoWidth = 160f
        const val infoHeight = 200f
    }
}