package tt.kao.sakuraprogress.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
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
        private const val MAX_PROGRESS = 100
        private const val PETAL_NUM = 15
        private const val INACTIVE_BAR_COLOR = R.color.colorMeadow
        private const val ACTIVE_BAR_COLOR = R.color.colorPetal
        private const val FLOWER_RING_COLOR = ACTIVE_BAR_COLOR
        private const val OUTER_BAR_PADDING = 5f
        private const val ONE_SHOT_ANIMATION_TIME = 3000L
        private const val FLAME_RENDER_INTERVAL = 150L
        private const val FLOWER_RING_WIDTH = 3f
        private const val MINIMAL_WIDTH_AND_HEIGHT = 50
    }

    private lateinit var flowerBitmap: Bitmap
    private lateinit var petalBitmap: Bitmap
    private lateinit var paintInactiveProgress: Paint
    private lateinit var paintActiveProgress: Paint
    private lateinit var paintFlowerRing: Paint
    private lateinit var paintBitmap: Paint
    private lateinit var inactiveLeftCapRect: RectF
    private lateinit var inactiveRightCapRect: RectF
    private lateinit var activeLeftCapRect: RectF
    private lateinit var activeRightCapRect: RectF
    private lateinit var inactiveBarRect: RectF
    private lateinit var imageAnimator: ValueAnimator

    private var rectFActiveBar: RectF = RectF()
    private var petalFalling = PetalFalling()

    // configurations
    private var inactiveBarColor: Int = 0
    private var activeBarColor: Int = 0
    private var flowerRingWidth: Float = 0f
    private var flowerRingColor: Int = 0
    private var maxProgress: Int = 0
    private var maxPetalNum: Int = 0

    // internal values
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
    private var flowerScale: Float = 0f
    private var flowerRotationDegree = 0f
    private var flowerRotationDirection = -1
    private var hasPetalFalling = false

    init {
        initAttributes(context, attrs)
        initPaints()
        initAnimator()
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SakuraProgress)

        try {
            inactiveBarColor = a.getColor(R.styleable.SakuraProgress_inactiveBarColor, ContextCompat.getColor(context, INACTIVE_BAR_COLOR))
            activeBarColor = a.getColor(R.styleable.SakuraProgress_activeBarColor, ContextCompat.getColor(context, ACTIVE_BAR_COLOR))
            flowerRingColor = a.getColor(R.styleable.SakuraProgress_flowerRingColor, ContextCompat.getColor(context, FLOWER_RING_COLOR))
            maxProgress = a.getInteger(R.styleable.SakuraProgress_maxProgress, MAX_PROGRESS)
            flowerRingWidth = a.getDimension(R.styleable.SakuraProgress_flowerRingWidth, ViewTool.dp2Px(context, FLOWER_RING_WIDTH))
            maxPetalNum = a.getInteger(R.styleable.SakuraProgress_petalNum, PETAL_NUM)
        } finally {
            a.recycle()
        }
    }

    private fun initPaints() {
        paintInactiveProgress = Paint(Paint.ANTI_ALIAS_FLAG)
        paintInactiveProgress.color = inactiveBarColor

        paintActiveProgress = Paint(Paint.ANTI_ALIAS_FLAG)
        paintActiveProgress.color = activeBarColor

        paintFlowerRing = Paint(Paint.ANTI_ALIAS_FLAG)
        paintFlowerRing.style = Paint.Style.STROKE
        paintFlowerRing.color = flowerRingColor
        paintFlowerRing.strokeWidth = ViewTool.dp2Px(context, 3f)

        paintBitmap = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG)
    }

    private fun initAnimator() {
        imageAnimator = ValueAnimator.ofFloat(0f, 1.6f * 360)
        imageAnimator.interpolator = CycleInterpolator(0.5f)
        imageAnimator.duration = ONE_SHOT_ANIMATION_TIME
        imageAnimator.addUpdateListener {
            val value = it.animatedValue as Float

            flowerRotationDegree = flowerRotationDirection * value

            postInvalidateDelayed(FLAME_RENDER_INTERVAL)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val defaultSize = ViewTool.dp2Px(context, MINIMAL_WIDTH_AND_HEIGHT.toFloat()).toInt()

        val width = ViewTool.measure(defaultSize, widthMeasureSpec)
        val height = ViewTool.measure(defaultSize, heightMeasureSpec)

        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        changeBound(w, h)
        loadBitmap()
        configurePetalFalling()
    }

    private fun changeBound(w: Int, h: Int) {
        viewWidth = w
        viewHeight = h
        progressWidth = viewWidth - paddingLeft - paddingRight
        progressHeight = viewHeight - paddingTop - paddingBottom
        progressRadius = progressHeight / 2f

        innerArcPadding = ViewTool.dp2Px(context, OUTER_BAR_PADDING).toInt()
        innerProgressWidth = progressWidth - 2 * innerArcPadding
        innerProgressHeight = progressHeight - 2 * innerArcPadding
        innerProgressRadius = progressRadius - innerArcPadding

        val progressDiameter = progressRadius * 2
        val innerProgressDiameter = innerProgressRadius * 2

        inactiveLeftCapRect = RectF(0f, 0f, progressDiameter, progressDiameter)
        inactiveRightCapRect = RectF(progressWidth - progressDiameter, 0f, progressWidth.toFloat(), progressDiameter)
        inactiveBarRect = RectF(progressRadius, 0f, progressWidth - progressRadius, progressHeight.toFloat())
        activeLeftCapRect = RectF(0f, 0f, innerProgressDiameter, innerProgressDiameter)
        activeRightCapRect = RectF(innerProgressWidth - innerProgressDiameter, 0f, innerProgressWidth.toFloat(), innerProgressDiameter)
    }

    private fun loadBitmap() {
        val innerProgressDiameter = innerProgressRadius * 2

        flowerBitmap = ViewTool.decodeResourceWithTarget(resources, R.drawable.sakura_flower, innerProgressDiameter.toInt(), innerProgressDiameter.toInt())
        petalBitmap = BitmapFactory.decodeResource(resources, R.drawable.sakura_petal)

        flowerScale = Math.min(innerProgressDiameter / flowerBitmap.width, innerProgressDiameter / flowerBitmap.height)
    }

    private fun configurePetalFalling() {
        // avoid the petal to exceed the area of inner bar
        val petalEdgeLength = Math.max(petalBitmap.width, petalBitmap.height)

        petalFalling.progressWidth = innerProgressWidth - petalEdgeLength - innerProgressRadius.toInt()
        petalFalling.progressHeight = innerProgressHeight - petalEdgeLength
        petalFalling.petalWidth = petalBitmap.width
        petalFalling.petalHeight = petalBitmap.height
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return

        val saveCount = canvas.save()
        canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())

        calculateProgressPosition()
        drawInactiveProgress(canvas)
        drawActiveProgress(canvas)
        drawPetal(canvas)
        drawImage(canvas)

        canvas.restoreToCount(saveCount)

        if (hasPetalFalling) postInvalidateDelayed(FLAME_RENDER_INTERVAL)
    }

    private fun calculateProgressPosition() {
        currentProgressPos = innerProgressWidth.toFloat() * progress / maxProgress
    }

    private fun drawInactiveProgress(canvas: Canvas) {
        canvas.drawArc(inactiveLeftCapRect, 90f, 180f, false, paintInactiveProgress)
        canvas.drawArc(inactiveRightCapRect, -90f, 180f, false, paintInactiveProgress)

        canvas.drawRect(inactiveBarRect, paintInactiveProgress)
    }

    private fun drawActiveProgress(canvas: Canvas) {
        val saveCount = canvas.save()
        canvas.translate(innerArcPadding.toFloat(), innerArcPadding.toFloat())

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

        hasPetalFalling = false
        val currentTime = System.currentTimeMillis()
        for (petal in petalFalling.petals) {
            if (currentTime < petal.startTime || petal.startTime == 0L) continue
            if (petal.x + petalBitmap.width < currentProgressPos) continue
            if (petal.x <= 0) continue

            val petalSaveCount = canvas.save()

            val matrix = petalFalling.newMatrix(petal, currentTime)

            canvas.drawBitmap(petalBitmap, matrix, paintBitmap)

            canvas.restoreToCount(petalSaveCount)
            hasPetalFalling = true
        }

        canvas.restoreToCount(saveCount)
    }

    private fun drawImage(canvas: Canvas) {
        val saveCount = canvas.save()

        canvas.drawArc(inactiveRightCapRect, 0f, 360f, false, paintInactiveProgress)
        canvas.drawArc(inactiveRightCapRect, 0f, 360f, false, paintFlowerRing)

        canvas.translate(innerArcPadding.toFloat(), innerArcPadding.toFloat())
        canvas.translate(activeRightCapRect.left, 0f)

        val matrix = Matrix()
        matrix.postRotate(
                flowerRotationDegree,
                flowerBitmap.width / 2f,
                flowerBitmap.height / 2f
        )
        matrix.postScale(flowerScale, flowerScale)
        canvas.drawBitmap(flowerBitmap, matrix, paintBitmap)

        canvas.restoreToCount(saveCount)
    }

    private fun startAnimation() {
        if (imageAnimator.isRunning) return

        flowerRotationDirection *= -1
        imageAnimator.start()

        bloomPetals()
    }

    private fun bloomPetals() {
        petalFalling.petalFallingStartTime = System.currentTimeMillis() + ONE_SHOT_ANIMATION_TIME
        petalFalling.bloom(maxPetalNum)
    }

    var progress: Int = 0
        get
        set(value) {
            field = value
            postInvalidate()

            startAnimation()
        }

    var petalNum: Int
        get() = maxPetalNum
        set(value) {
            maxPetalNum = value
        }

    var petalFloatTime: Int
        get() = petalFalling.floatTime
        set(value) {
            petalFalling.floatTime = value
        }

    var petalRotateTime: Int
        get() = petalFalling.rotateTime
        set(value) {
            petalFalling.rotateTime = value
        }

}
