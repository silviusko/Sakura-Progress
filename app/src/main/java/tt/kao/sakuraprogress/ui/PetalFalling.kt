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
        private const val PETAL_AMPLITUDE_TIME = 1000
    }

    private val random = Random()
    private var newFallingTime: Long = 0

    var progressWidth: Int = 0
    var progressHeight: Int = 0
    var petalWidth: Int = 0
    var petalHeight: Int = 0
    val petals = ArrayList<Petal>()

    fun build(num: Int) {
        petals.clear()

        for (i in 0 until num) {
            newFallingTime += random.nextInt(PETAL_FLOAT_TIME * 2).toLong()

            val petal = Petal(
                    id = i,
                    angle = random.nextInt(360),
                    direction = if (random.nextBoolean()) 1 else -1,
                    startTime = System.currentTimeMillis() + newFallingTime,
                    amplitude = random.nextFloat()
            )
            petals.add(petal)
        }
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

        if (intervalTime > PETAL_FLOAT_TIME) {
            petal.startTime = System.currentTimeMillis() + random.nextInt(PETAL_FLOAT_TIME)
        }

        val factor = intervalTime.toFloat() / PETAL_FLOAT_TIME
        petal.x = progressWidth - progressWidth * factor
        petal.y = if (petal.y == 0f) random.nextFloat() * progressHeight else petal.y

        calculateAmplitude(currentTime, petal, matrix)

        matrix.postTranslate(petal.x, petal.y)
        if (petal.id == 1) {
            Log.d("SakuraProgress", "id:${petal.id} = x:${petal.x}, y:${petal.y}")
        }
    }

    private fun calculateAmplitude(currentTime: Long, petal: Petal, matrix: Matrix) {
        val intervalTime = currentTime - petal.startTime
        if (intervalTime < 0) return

        val w = 2f * Math.PI / progressWidth
        val h = petal.id * 2f * Math.PI / 100
        petal.y = (Math.sin(w * petal.x + h) * petal.amplitude * progressHeight / 2 + progressHeight / 2f).toFloat()

    }

    private fun calculateRotation(currentTime: Long, petal: Petal, matrix: Matrix) {
        val rotateFactor = (currentTime - petal.startTime) % PETAL_ROTATION_TIME / PETAL_ROTATION_TIME.toFloat()
        val angle = rotateFactor * 360 * petal.direction
        val rotate = petal.angle + angle

        matrix.postRotate(rotate, petal.x + petalWidth / 2f, petal.y + petalHeight / 2f)
    }
}