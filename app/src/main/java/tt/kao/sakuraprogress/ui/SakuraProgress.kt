package tt.kao.sakuraprogress.ui

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import tt.kao.sakuraprogress.R
import java.util.*

/**
 * @author luke_kao
 */
class SakuraProgress @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : View(context, attrs) {

    companion object {
        private const val PROGRESS_INACTIVE_COLOR = "#81ed07"
        private const val PROGRESS_ACTIVE_COLOR = "#FF7C80"
        private const val MAX_PROGRESS = 100
        private const val INNER_PADDING = 5f
    }

    private lateinit var rotatedDrawable: Drawable
    private lateinit var petalBitmap: Bitmap
    private lateinit var paintInactiveProgress: Paint
    private lateinit var paintActiveProgress: Paint
    private lateinit var paintBitmap: Paint
    private lateinit var rectFInactiveCap: RectF
    private lateinit var rectFActiveCap: RectF
    private lateinit var rectFInactiveBar: RectF
    private lateinit var rectfDrawable: RectF

    private var petalFalling = PetalFalling()

    private var colorInactiveProgress: Int
    private var colorActiveProgress: Int
    private var maxProgress: Int
    private var viewWidth: Int = 0
    private var viewHeight: Int = 0
    private var progressWidth: Int = 0
    private var progressHeight: Int = 0
    private var progressRadius: Float = 0f
    private var currentProgressPos: Float = 0f
    private var innerArcPadding: Int = 0
    private var innerProgressWidth: Int = 0
    private var innerProgressHeight: Int = 0
    private var innerProgressRadius: Float = 0f

    var progress: Int = 0
        get
        set(value) {
            field = value
            postInvalidate()
        }

    private var rectFActiveBar: RectF = RectF()

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SakuraProgress)
        try {
            colorInactiveProgress = a.getColor(R.styleable.SakuraProgress_progressInactiveColor, Color.parseColor(PROGRESS_INACTIVE_COLOR))
            colorActiveProgress = a.getColor(R.styleable.SakuraProgress_progressActiveColor, Color.parseColor(PROGRESS_ACTIVE_COLOR))
            maxProgress = a.getInteger(R.styleable.SakuraProgress_maxProgress, MAX_PROGRESS)
        } finally {
            a.recycle()
        }

        initBitmap()
        initPaints()
        initPetals()
    }

    private fun initBitmap() {
//        rotatedDrawable = ContextCompat.getDrawable(context, R.mipmap.ic_launcher_round)
        petalBitmap = BitmapFactory.decodeResource(resources, R.drawable.petal)
    }


    private fun initPaints() {
        paintInactiveProgress = Paint()
        paintInactiveProgress.isAntiAlias = true
        paintInactiveProgress.color = colorInactiveProgress

        paintActiveProgress = Paint()
        paintActiveProgress.isAntiAlias = true
        paintActiveProgress.color = colorActiveProgress

        paintBitmap = Paint()
        paintBitmap.isAntiAlias = true
        paintBitmap.isDither = true
        paintBitmap.isFilterBitmap = true
    }

    private fun initPetals() {
        petalFalling.build(30)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        viewWidth = w
        viewHeight = h
        progressWidth = viewWidth - paddingLeft - paddingRight
        progressHeight = viewHeight - paddingTop - paddingBottom
        progressRadius = progressHeight / 2f

        innerArcPadding = dp2px(INNER_PADDING)
        innerProgressWidth = progressWidth - 2 * innerArcPadding
        innerProgressHeight = progressHeight - 2 * innerArcPadding
        innerProgressRadius = progressRadius - innerArcPadding

        rectFInactiveCap = RectF(0f, 0f, progressRadius * 2, progressRadius * 2)
        rectFActiveCap = RectF(0f, 0f, innerProgressRadius * 2, innerProgressRadius * 2)
        rectFInactiveBar = RectF(progressRadius, 0f, progressWidth - progressRadius, progressHeight.toFloat())

        petalFalling.progressWidth = innerProgressWidth - petalBitmap.width
        petalFalling.progressHeight = innerProgressHeight - petalBitmap.height
        petalFalling.petalWidth = petalBitmap.width
        petalFalling.petalHeight = petalBitmap.height
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return

        val saveCount = canvas.save()
        canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())

        drawInactiveProgress(canvas)
        drawActiveProgress(canvas)
        drawDrawable(canvas)

        canvas.restoreToCount(saveCount)

        postInvalidate()
    }

    private fun drawInactiveProgress(canvas: Canvas) {
        canvas.drawArc(rectFInactiveCap, 90f, 180f, false, paintInactiveProgress)

        canvas.drawRect(rectFInactiveBar, paintInactiveProgress)
    }

    private fun drawActiveProgress(canvas: Canvas) {
        val saveCount = canvas.save()
        canvas.translate(innerArcPadding.toFloat(), innerArcPadding.toFloat())

        drawInnerBar(canvas)

        drawPetal(canvas)

        canvas.restoreToCount(saveCount)
    }

    private fun drawInnerBar(canvas: Canvas) {
        currentProgressPos = innerProgressWidth.toFloat() * progress / maxProgress

        if (currentProgressPos > innerProgressRadius) {
            rectFActiveBar.set(innerProgressRadius, 0f, currentProgressPos, innerProgressHeight.toFloat())

            canvas.drawArc(rectFActiveCap, 90f, 180f, false, paintActiveProgress)
            canvas.drawRect(rectFActiveBar, paintActiveProgress)
        } else {
            val remainDistance = innerProgressRadius - currentProgressPos
            val degree = Math.toDegrees(Math.acos(remainDistance.toDouble() / innerProgressRadius)).toFloat()
            val startAngle = 180f - degree
            val sweepAngle = degree * 2

            canvas.drawArc(rectFActiveCap, startAngle, sweepAngle, false, paintActiveProgress)
        }
    }

    private fun drawPetal(canvas: Canvas) {
        val currentTime = System.currentTimeMillis()
        for (petal in petalFalling.petals) {
            if (currentTime < petal.startTime || petal.startTime == 0L) continue

            val saveCount = canvas.save()

            val matrix = petalFalling.newMatrix(petal, currentTime)
            canvas.drawBitmap(petalBitmap, matrix, paintBitmap)

            canvas.restoreToCount(saveCount)
        }
    }

    private fun drawDrawable(canvas: Canvas) {
    }

    private fun dp2px(dp: Float): Int {
        return (resources.displayMetrics.density * dp).toInt()
    }
}

internal data class Petal(
        val id: Int,
        var x: Float = 0f,
        var y: Float = 0f,
        var angle: Int,
        var direction: Int,
        var startTime: Long = 0
)

internal class PetalFalling {

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
                    startTime = System.currentTimeMillis() + newFallingTime
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

        matrix.postTranslate(0f, petal.y - progressHeight)

        val w = 2f * Math.PI / progressWidth
        petal.y = (11 * Math.sin(w * petal.x) + progressHeight / 3).toFloat()

    }

    private fun calculateRotation(currentTime: Long, petal: Petal, matrix: Matrix) {
        val rotateFactor = (currentTime - petal.startTime) % PETAL_ROTATION_TIME / PETAL_ROTATION_TIME.toFloat()
        val angle = rotateFactor * 360 * petal.direction
        val rotate = petal.angle + angle

        matrix.postRotate(rotate, petal.x + petalWidth / 2f, petal.y + petalHeight / 2f)
    }
}

