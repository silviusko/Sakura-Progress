package tt.kao.sakuraprogress.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.CycleInterpolator
import tt.kao.sakuraprogress.R

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
        private const val PETAL_NUM = 50
    }

    private lateinit var imageBitmap: Bitmap
    private lateinit var petalBitmap: Bitmap
    private lateinit var paintInactiveProgress: Paint
    private lateinit var paintActiveProgress: Paint
    private lateinit var paintBitmap: Paint
    private lateinit var inactiveLeftCapRect: RectF
    private lateinit var inactiveRightCapRect: RectF
    private lateinit var activeLeftCapRect: RectF
    private lateinit var activeRightCapRect: RectF
    private lateinit var inactiveBarRect: RectF
    private lateinit var imageAnimator: ValueAnimator

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
    private var imageScale: Float = 0f
    private var imageRotationDegree = 0f
    private var imageRotationDirection = -1

    var progress: Int = 0
        get
        set(value) {
            field = value
            postInvalidate()

            if (!imageAnimator.isRunning) {
                imageRotationDirection *= -1
                imageAnimator.start()
            }
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
        imageBitmap = BitmapFactory.decodeResource(resources, R.drawable.sakura_flower)
        petalBitmap = BitmapFactory.decodeResource(resources, R.drawable.sakura_petal)
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
        petalFalling.build(PETAL_NUM)
    }

    private fun initAnimator() {
        imageAnimator = ValueAnimator.ofFloat(0f, 1.6f * 360)
        imageAnimator.interpolator = CycleInterpolator(0.5f)
        imageAnimator.duration = 3000
        imageAnimator.addUpdateListener {
            val value = it.animatedValue as Float

            imageRotationDegree = imageRotationDirection * value

            postInvalidate()
        }
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

        inactiveLeftCapRect = RectF(0f, 0f, progressRadius * 2, progressRadius * 2)
        inactiveRightCapRect = RectF(progressWidth - progressRadius * 2f, 0f, progressWidth.toFloat(), progressRadius * 2)
        inactiveBarRect = RectF(progressRadius, 0f, progressWidth - progressRadius, progressHeight.toFloat())
        activeLeftCapRect = RectF(0f, 0f, innerProgressRadius * 2, innerProgressRadius * 2)
        activeRightCapRect = RectF(innerProgressWidth - innerProgressRadius * 2, 0f, innerProgressWidth.toFloat(), innerProgressRadius * 2)

        // avoid the petal to exceed the area of inner bar
        val petalEdgeLength = Math.max(petalBitmap.width, petalBitmap.height)

        petalFalling.progressWidth = innerProgressWidth - petalEdgeLength - innerProgressRadius.toInt()
        petalFalling.progressHeight = innerProgressHeight - petalEdgeLength
        petalFalling.petalWidth = petalBitmap.width
        petalFalling.petalHeight = petalBitmap.height

        imageScale = Math.min(innerProgressRadius * 2 / imageBitmap.width, innerProgressRadius * 2 / imageBitmap.height)

        initAnimator()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return

        val saveCount = canvas.save()
        canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())

        drawInactiveProgress(canvas)
        drawActiveProgress(canvas)
        drawPetal(canvas)
        drawImage(canvas)

        canvas.restoreToCount(saveCount)

        postInvalidate()
    }

    private fun drawInactiveProgress(canvas: Canvas) {
        canvas.drawArc(inactiveLeftCapRect, 90f, 180f, false, paintInactiveProgress)
        canvas.drawArc(inactiveRightCapRect, -90f, 180f, false, paintInactiveProgress)

        canvas.drawRect(inactiveBarRect, paintInactiveProgress)
    }

    private fun drawActiveProgress(canvas: Canvas) {
        val saveCount = canvas.save()
        canvas.translate(innerArcPadding.toFloat(), innerArcPadding.toFloat())

        currentProgressPos = innerProgressWidth.toFloat() * progress / maxProgress

        if (currentProgressPos > innerProgressRadius) {
            var activeBarMaxPos = currentProgressPos
            if (activeBarMaxPos > activeRightCapRect.centerX()) {
                activeBarMaxPos = activeRightCapRect.centerX()
            }
            rectFActiveBar.set(innerProgressRadius, 0f, activeBarMaxPos, innerProgressHeight.toFloat())

            canvas.drawArc(activeLeftCapRect, 90f, 180f, false, paintActiveProgress)
            canvas.drawRect(rectFActiveBar, paintActiveProgress)
        } else {
            val remainDistance = innerProgressRadius - currentProgressPos
            val degree = Math.toDegrees(Math.acos(remainDistance.toDouble() / innerProgressRadius)).toFloat()
            val startAngle = 180f - degree
            val sweepAngle = degree * 2

            canvas.drawArc(activeLeftCapRect, startAngle, sweepAngle, false, paintActiveProgress)
        }

        canvas.restoreToCount(saveCount)
    }

    private fun drawPetal(canvas: Canvas) {
        val saveCount = canvas.save()
        canvas.translate(innerArcPadding.toFloat(), innerArcPadding.toFloat())
        canvas.translate(0f, petalBitmap.height / 2f)

        val currentTime = System.currentTimeMillis()
        for (petal in petalFalling.petals) {
            if (currentTime < petal.startTime || petal.startTime == 0L) continue

            val petalSaveCount = canvas.save()

            val matrix = petalFalling.newMatrix(petal, currentTime)

            canvas.drawBitmap(petalBitmap, matrix, paintBitmap)

            canvas.restoreToCount(petalSaveCount)
        }

        canvas.restoreToCount(saveCount)
    }

    private fun drawImage(canvas: Canvas) {
        val saveCount = canvas.save()
        canvas.translate(innerArcPadding.toFloat(), innerArcPadding.toFloat())

        canvas.drawArc(activeRightCapRect, 0f, 360f, false, paintInactiveProgress)

        canvas.translate(activeRightCapRect.left, 0f)

        val matrix = Matrix()
        matrix.postRotate(
                imageRotationDegree,
                imageBitmap.width / 2f,
                imageBitmap.height / 2f
        )
        matrix.postScale(imageScale, imageScale)
        canvas.drawBitmap(imageBitmap, matrix, paintBitmap)

        canvas.restoreToCount(saveCount)
    }

    private fun dp2px(dp: Float): Int {
        return (resources.displayMetrics.density * dp).toInt()
    }
}
