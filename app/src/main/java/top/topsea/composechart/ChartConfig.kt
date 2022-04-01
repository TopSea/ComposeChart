package top.topsea.composechart

data class ChartConfig(
    var gridSize: Int
) {
    companion object {
        const val horPadding = 80f
        const val verPadding = 60f
    }
}