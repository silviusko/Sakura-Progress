package tt.kao.sakuraprogress.ui

/**
 * @author luke_kao
 */
data class Petal(
        val id: Int,
        var x: Float = 0f,
        var y: Float = 0f,
        var angle: Int,
        var direction: Int,
        var startTime: Long = 0,
        var amplitude: Float
)
