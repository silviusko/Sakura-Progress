package tt.kao.sakuraprogress.ui

import android.graphics.Matrix
import android.util.Log
import java.util.*

/**
 * @author luke_kao
 */
class PetalFalling {

    companion object {
        private const val PETAL_FLOAT_TIME = 3000
        private const val PETAL_ROTATION_TIME = 2000
    }

    private val random = Random()
    private var count = 0

    var progressWidth: Int = 0
    var progressHeight: Int = 0
    var petalWidth: Int = 0
    var petalHeight: Int = 0
    val petals = ArrayList<Petal>()
    var petalFallingStartTime = 0L

    fun bloom(num: Int) {
        prune()

        for (i in 0 until num) {
            val petal = Petal(
                    id = count++,
                    angle = random.nextInt(360),
                    direction = if (random.nextBoolean()) 1 else -1,
                    startTime = System.currentTimeMillis() + random.nextInt(PETAL_FLOAT_TIME),
                    amplitudeSeed = random.nextInt(num) + 1,
                    x = progressWidth.toFloat()
            )
            petals.add(petal)
        }
        Log.d("PetalFalling", "Petals size:${petals.size}")
    }

    private fun prune() {
        petals.filter { it.x <= 0 }
                .forEach { petals.remove(it) }
    }

    fun newMatrix(petal: Petal, currentTime: Long): Matrix {
        val matrix = Matrix()

        calculateLocation(currentTime, petal, matrix)
        calculateRotation(currentTime, petal, matrix)

        return matrix
    }

    private fun calculateLocation(currentTime: Long, petal: Petal, matrix: Matrix) {
        val intervalTime = currentTime - petal.startTime
        if (intervalTime < 0) return

        val fraction = intervalTime.toFloat() / PETAL_FLOAT_TIME
        petal.x = progressWidth - progressWidth * fraction

        calculateAmplitude(currentTime, petal)

        matrix.postTranslate(petal.x, petal.y)
//        Log.d("SakuraProgress", "x:${petal.x}, y:${petal.y}")
    }

    private fun calculateAmplitude(currentTime: Long, petal: Petal) {
        val intervalTime = currentTime - petal.startTime
        if (intervalTime < 0) return

        val w = 2f * Math.PI / progressWidth
        val h = petal.amplitudeSeed * Math.PI / 10

        petal.y = (Math.sin(w * petal.x + h) * progressHeight / 2 + progressHeight / 2f).toFloat()

    }

    private fun calculateRotation(currentTime: Long, petal: Petal, matrix: Matrix) {
        val rotateFactor = (currentTime - petal.startTime) % PETAL_ROTATION_TIME / PETAL_ROTATION_TIME.toFloat()
        val angle = rotateFactor * 360 * petal.direction
        val rotate = petal.angle + angle

        matrix.postRotate(rotate, petal.x + petalWidth / 2f, petal.y + petalHeight / 2f)
    }
}