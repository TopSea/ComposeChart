package top.topsea.composechart

data class ChartConfig(
    var gridSize: Int
) {
    companion object {
        const val horPadding = 80f
        const val verPadding = 40f
        var gridSize = 100f
        const val infoWidth = 160f
        const val infoHeight = 200f
    }
}