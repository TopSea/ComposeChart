package top.topsea.composechart

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.NativePaint
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle

data class ChartConfig(
    var coordinate: CoordinateConfig,
    var line: LineConfig,
    var scrollable: Boolean = true,
    var scalable: Boolean = true,
    var dotClickable: Boolean = true,
    var scaleLimit: IntRange? = null
) {

    init {
        if (!scalable) {
            if (scaleLimit != null) {
                scaleLimit = null
            }
        } else {
            if (scaleLimit == null) {
                scaleLimit = IntRange(50, 200)
            }
        }
    }
    companion object {
        const val horPadding = 80f
        const val verPadding = 60f
        var gridSize = mutableStateOf(100f)
        const val infoWidth = 160f
        const val infoHeight = 200f
    }
}

data class CoordinateConfig(
    val gridSize: MutableState<Float> = mutableStateOf(100f),
    var withText: Boolean = true,
    var withGrid: Boolean = true,
    var withArrow: Boolean = true,
    val textPaint: android.graphics.Paint = NativePaint().apply {
        color = android.graphics.Color.BLACK
        style = android.graphics.Paint.Style.FILL
        strokeWidth = 1f
        textSize = 24f
    },
    val axisPaint: Paint = Paint().apply {
        color = Color.LightGray
        style = PaintingStyle.Stroke
        strokeWidth = 5f
    },
)

data class LineConfig(
    val listValue: SnapshotStateList<Float>,
    var withDot: Boolean = true,
    var withInfo: Boolean = true,
    var listInfo: SnapshotStateList<Float>? = null,
    var textPaint: android.graphics.Paint? = null,
    var infoWidth: Float? = null,
    var textHeight: Float? = null,
    val axisPaint: Paint = Paint().apply {
        color = Color.Red
        style = PaintingStyle.Stroke
        strokeWidth = 5f
    },
) {
    init {
        if (listValue.size < 1) {
            throw RuntimeException("Must have values!!!")
        }
        if (withInfo && !withDot) {
            withDot = true
        }
        if (withDot && (listInfo == null || listInfo!!.size < 1)) {
            throw RuntimeException("Must have info!!!")
        }
        if (listInfo != null && listInfo!!.size > 1) {
            textPaint = NativePaint().apply {
                color = android.graphics.Color.BLACK
                style = android.graphics.Paint.Style.FILL
                strokeWidth = 1f
                textSize = 24f
            }
            infoWidth = 160f
            textHeight = 200f
        } else {
            //没有就别占内存了
            textPaint = null
            infoWidth = null
            textHeight = null
        }
    }
}
